*=$0801
BASIC:	!BYTE $0B,$08,$01,$00,$9E,$32,$30,$36,$31,$00,$00,$00
	JMP init

vqsplo	= $40
vqsphi	= $41
vqeplo	= $42
vqephi	= $43
seed0	= $44
seed1	= $45
seed2	= $46
seed3	= $47
tmp0	= $48
tmp1	= $49
tmp2	= $4A
tmp3	= $4B
mod	= $4C
modhi	= $4D
rnd	= $4E
rndhi	= $4F
shsplo	= $50
shsphi	= $51
sheplo	= $52
shephi	= $53
shlnlo	= $54
shlnhi	= $55

ggraph	= $8000
ggvstd	= $8020
ggdstl	= $8040
ggdsth	= $8060
ggprvx	= $8080
ggprvy	= $80A0
vqueue	= $9800
pqueue	= $9C00
via1ta	= $9F04
via1tb	= $9F05
via1tc	= $9F08
via1td	= $9F09
veraal	= $9F20
veraam	= $9F21
veraah	= $9F22
verad0	= $9F23
veract	= $9F25
screen	= $FF5F
chrout	= $FFD2
getin	= $FFE4



; ******************
; Main Gameplay Loop
; ******************

	; Set 40x30 mode, PETSCII mode, lowercase mode.
init	lda #$03
	jsr screen
	lda #$8F
	jsr chrout
	lda #$0E
	jsr chrout

	; Initialize game state from scratch.
	lda via1ta
	and #$01
	sta gstate
	lda #$FF
	sta aiplay
	lda #$01
	sta brdsiz
	jsr cfgbrd
	jsr inibrd
	jsr seed

	; Display help message.
	jmp help

	; Game state.
gstate	!byte $01
aiplay	!byte $FF
brdsiz	!byte $01

	; Initialize game state not from scratch.
init2	lda via1ta
	and #$01
	sta gstate
	lda brdsiz
	jsr cfgbrd
	jsr inibrd

	; Draw game display from scratch.
cont	lda #$93
	jsr chrout
	lda gstate
	jsr prthdr
	jsr prtbrd
	ldx #1
	ldy #29
	lda #<statl2
	sta prtsp+1
	lda #>statl2
	sta prtsp+2
	jsr prtstr
	bra gamepl

	; Draw game display not from scratch.
cont2	lda gstate
	jsr prthdr
	jsr updbrd

	; Print prompt.
gamepl	lda gstate
	cmp #$04
	bcs gamep2
	lda #<prmcol
	sta prtsp+1
	lda #>prmcol
	sta prtsp+2
	bcc gamep3
gamep2	lda #<prmcmd
	sta prtsp+1
	lda #>prmcmd
	sta prtsp+2
gamep3	jsr prtprm

	; Wait for command.
gamekl	jsr getin
	and #$7F
	cmp #$5B
	bcs gamekl
	cmp #$41
	bcs gamemm
	cmp #$3A
	bcs gamekl
	cmp #$31
	bcc gamekl
	sbc #$31
	asl
	asl
	tay
	lda gamedt,y
	sta prtsp+1
	iny
	lda gamedt,y
	sta prtsp+2
	beq gamek2
	phy
	jsr prmyn
	ply
	bcs gamepl
gamek2	iny
	lda gamedt,y
	sta gamek3+1
	iny
	lda gamedt,y
	sta gamek3+2
gamek3	jmp gamepl

	; Column number was entered. Wait for row number.
gamemm	ldx #34
	sta prmrow,x
	sbc #$41
	pha
	lda gstate
	cmp #$04
	bcs gamemx
	lda #<prmrow
	sta prtsp+1
	lda #>prmrow
	sta prtsp+2
	jsr prmnum
	bcs gamemx
	tay
	dey
	plx
	; Make a move.
	lda gstate
	ror
	jsr mkmove
	bcs gamemi
	; Check for path.
	lda gstate
	ror
	jsr pathex
	bcc gamemw
	; Switch to next player.
	lda gstate
	and #$01
	eor #$01
	ora #$02
	sta gstate
	lda aiplay
	bne gameai
	jmp cont2
	; Game over or input cancelled; return to prompt.
gamemx	pla
	jmp gamepl
	; Invalid move; print message and return to prompt.
gamemi	lda #<prminv
	sta prtsp+1
	lda #>prminv
	sta prtsp+2
	jsr prmyn
	jmp gamepl
	; Move ended the game.
gamemw	lda gstate
	and #$01
	ora #$04
	sta gstate
	jmp cont2

	; Computer player path.
	; Draw game display.
gameai	lda gstate
	jsr prthdr
	jsr updbrd
	; Print "prompt."
	lda #<prmai
	sta prtsp+1
	lda #>prmai
	sta prtsp+2
	jsr prtprm
	; Get the computer's move.
gameam	jsr gtaimv
	; Make a move.
	lda gstate
	ror
	jsr mkmove
	bcs gameam
	; Check for path.
	lda gstate
	ror
	jsr pathex
	bcc gamemw
	; Switch to next player.
	lda gstate
	and #$01
	eor #$01
	ora #$02
	sta gstate
	jmp cont2

	; dispatch table for game command prompt
gamedt	!byte <prmn1p, >prmn1p, <new1p, >new1p
	!byte <prmn2p, >prmn2p, <new2p, >new2p
	!byte <prmsml, >prmsml, <newsml, >newsml
	!byte <prmmed, >prmmed, <newmed, >newmed
	!byte <prmlrg, >prmlrg, <newlrg, >newlrg
	!byte <prmhug, >prmhug, <newhug, >newhug
	!byte <prmgig, >prmgig, <newgig, >newgig
	!byte 0, 0, <help, >help
	!byte <prmex, >prmex, <quit, >quit

new1p	lda #$FF
	sta aiplay
	jmp init2

new2p	lda #$00
	sta aiplay
	jmp init2

newsml	lda #$00
	sta brdsiz
	jmp init2

newmed	lda #$01
	sta brdsiz
	jmp init2

newlrg	lda #$02
	sta brdsiz
	jmp init2

newhug	lda #$03
	sta brdsiz
	jmp init2

newgig	lda #$04
	sta brdsiz
	jmp init2

quit	lda #$00
	jsr screen
	lda #$8F
	jsr chrout
	lda #$8E
	jmp chrout



; ***********
; Help Screen
; ***********

	; clear screen
help	lda #$93
	jsr chrout

	; print title
	lda #$00
	sta veract
	lda #$11
	sta veraah
	lda #$B0
	sta veraam
	lda #$02
	sta veraal
	ldy #7
title1	lda #$58
	sta verad0
	lda #$2A
	sta verad0
	lda #$4F
	sta verad0
	lda #$5D
	sta verad0
	dey
	bne title1
	ldy #9
title2	lda #$20
	sta verad0
	lda #$98
	sta verad0
	dey
	bne title2
	ldy #7
title3	lda #$4F
	sta verad0
	lda #$5D
	sta verad0
	lda #$58
	sta verad0
	lda #$2A
	sta verad0
	dey
	bne title3
	ldx #16
	ldy #0
	lda #<title
	sta prtsp+1
	lda #>title
	sta prtsp+2
	jsr prtstr

	; print content
	ldy #$00
help1	lda helpvc,y
	beq help2
	tax
	iny
	lda helpvc,y
	beq help2
	pha
	iny
	lda helpvc,y
	sta prtsp+1
	iny
	lda helpvc,y
	sta prtsp+2
	pla
	phy
	tay
	jsr prtstr
	ply
	iny
	bne help1

	; print status line
help2	ldx #1
	ldy #29
	lda #<statl1
	sta prtsp+1
	lda #>statl1
	sta prtsp+2
	jsr prtstr

	; print prompt
helppl	lda #<prmcmd
	sta prtsp+1
	lda #>prmcmd
	sta prtsp+2
	jsr prtprm

	; wait for command
helpkl	jsr getin
	and #$7F
	cmp #$3A
	bcs helpkl
	cmp #$31
	bcc helpkl
	sbc #$31
	asl
	asl
	tay
	lda helpdt,y
	sta prtsp+1
	iny
	lda helpdt,y
	sta prtsp+2
	beq helpk2
	phy
	jsr prmyn
	ply
	bcs helppl
helpk2	iny
	lda helpdt,y
	sta helpk3+1
	iny
	lda helpdt,y
	sta helpk3+2
helpk3	jmp helppl

	; dispatch table for help command prompt
helpdt	!byte <prmn1p, >prmn1p, <new1p, >new1p
	!byte <prmn2p, >prmn2p, <new2p, >new2p
	!byte <prmsml, >prmsml, <newsml, >newsml
	!byte <prmmed, >prmmed, <newmed, >newmed
	!byte <prmlrg, >prmlrg, <newlrg, >newlrg
	!byte <prmhug, >prmhug, <newhug, >newhug
	!byte <prmgig, >prmgig, <newgig, >newgig
	!byte 0, 0, <cont, >cont
	!byte <prmex, >prmex, <quit, >quit



; *******************
; Utility Subroutines
; *******************

; Sets a character or attribute in video memory.
; A = character or attribute, X = column, Y = row
; Carry clear sets the character, carry set sets the attribute.
; Returns with A, X, Y unchanged and carry clear.
setchr	pha
	lda #$00
	sta veract
	txa
	rol
	sta veraal
	tya
	clc
	adc #$B0
	sta veraam
	lda #$01
	sta veraah
	pla
	sta verad0
	rts

; Print a string.
; prtsp+1 = string address, X = column, Y = row
prtstr	lda #$00
	sta veract
	txa
	asl
	sta veraal
	tya
	clc
	adc #$B0
	sta veraam
	lda #$21
	sta veraah
	ldy #$00
prtsp	lda prtnul,y
	beq prtex
	sta verad0
	iny
	bne prtsp
prtex	rts
prtnul	brk

; Clear a line.
; A = attribute, Y = row
clrlin	pha
	lda #$00
	sta veract
	lda #$02
	sta veraal
	tya
	clc
	adc #$B0
	sta veraam
	lda #$11
	sta veraah
	pla
	ldx #37
	ldy #$20
clrllp	sty verad0
	sta verad0
	dex
	bne clrllp
	rts

; Print a prompt.
; prtsp+1 = string address
prtprm	lda #$61
	ldy #28
	jsr clrlin
	ldx #1
	ldy #28
	jmp prtstr

; Print a prompt and wait for Y/N.
; Returns carry clear for Y, carry set for N.
prmyn	jsr prtprm
prmyn1	jsr getin
	and #$7F
	cmp #$59	; Y
	beq prmyn2
	cmp #$4E	; N
	bne prmyn1
	sec
	rts
prmyn2	clc
	rts

; Print a prompt and wait for a two-digit number.
; Returns carry clear and number in A, or carry set if cancelled.
prmnum	jsr prtprm
	lda #$00
	sta prmnmp
	lda #$20
	sta prmnmt
	sta prmnmo
prmnmu	ldy #28
	ldx #36
	lda prmnmt
	clc
	jsr setchr
	inx
	lda prmnmo
	jsr setchr
prmnml	jsr getin
	cmp #$03	; stop
	beq prmnmx
	cmp #$08	; backspace
	beq prmnmb
	cmp #$14	; del
	beq prmnmb
	cmp #$1B	; esc
	beq prmnmx
	and #$7F
	cmp #$0D	; return
	beq prmnmr
	cmp #$13	; home/clear
	beq prmnmx
	cmp #$5F	; back arrow
	beq prmnmb
	cmp #$30	; digit
	bcc prmnml
	cmp #$3A
	bcs prmnml
	ldx prmnmp
	cpx #$02
	bcs prmnml
	sta prmnmt,x
	inc prmnmp
	bra prmnmu
prmnmb	ldx prmnmp
	beq prmnmx
	dex
	lda #$20
	sta prmnmt,x
	stx prmnmp
	bra prmnmu
prmnmr	ldx prmnmp
	beq prmnml
	lda prmnmt
	and #$0F
	dex
	beq prmnma
	asl
	sta prmnmt
	asl
	asl
	adc prmnmt
	sta prmnmt
	lda prmnmo
	and #$0F
	adc prmnmt
prmnma	clc
	rts
prmnmx	sec
	rts
prmnmp	!byte $00	; place
prmnmt	!byte $20	; tens
prmnmo	!byte $20	; ones

; Print a header message.
prthdr	asl
	asl
	tax
	lda movevc,x
	phx
	ldy #$00
	jsr clrlin
	plx
	inx
	lda movevc,x
	pha
	inx
	lda movevc,x
	sta prtsp+1
	inx
	lda movevc,x
	sta prtsp+2
	plx
	ldy #$00
	jmp prtstr

; Print a BCD number.
prtbcd	pha
	lsr
	lsr
	lsr
	lsr
	clc
	ora #$30
	jsr setchr
	pla
	inx
	pha
	and #$0F
	ora #$30
	jsr setchr
	pla
	dex
	rts

; Increment a BCD number.
incbcd	inc
	pha
	and #$0F
	cmp #$0A
	bcs incbc1
	pla
	rts
incbc1	pla
	adc #$05
	rts



; ******************
; Game Board Display
; ******************

; Print board row numbers
prtbrw	php
	clc
	lda #$01
prbry0	ldy #3
prbrx0	ldx #6
	jsr prtbcd
prbrx1	ldx #31
	jsr prtbcd
	jsr incbcd
	plp
	bcc prtbr2
	iny
prtbr2	iny
	php
prbry1	cpy #26
	bcc prbrx0
	plp
	rts

; Print board column letters
prtbcl	php
	clc
	lda #$41
prbcx0	ldx #8
prbcy0	ldy #2
	jsr setchr
prbcy1	ldy #26
	jsr setchr
	inc
	plp
	bcc prtbc2
	inx
prtbc2	inx
	php
prbcx1	cpx #31
	bcc prbcy0
	plp
	rts

; Print board background.
prtbbk	php
prbby0	ldy #3
prbbx0	ldx #8
prtbb2	sec
	lda #$EE
	jsr setchr
	lda #$20
	jsr setchr
	inx
prbbx1	cpx #31
	bcc prtbb2
	iny
prbby1	cpy #26
	bcc prbbx0
	plp
	rts

; Print a piece of the board.
prtbpc	pha
	php
	and #$F0
	cmp #$90
	beq prtbxm
	cmp #$A0
	beq prtbxh
	cmp #$B0
	beq prtbxv
	cmp #$D0
	beq prtbom
	cmp #$E0
	beq prtboh
	cmp #$F0
	beq prtbov
prtbpx	plp
	pla
	rts
prtbxm	lda #$2A
	jsr setchr
	lda #$58
	jsr setchr
	bra prtbpx
prtbom	lda #$5D
	jsr setchr
	lda #$4F
	jsr setchr
	bra prtbpx
prtbxh	lda #$E2
	sta prtbpa
	bra prtbph
prtboh	lda #$E5
	sta prtbpa
	bra prtbph
prtbxv	lda #$E2
	sta prtbpa
	bra prtbpv
prtbov	lda #$E5
	sta prtbpa
	bra prtbpv
prtbph	lda prtbpa
	jsr setchr
	lda #$40
	jsr setchr
	plp
	bcc prtbpy
	dex
	lda prtbpa
	jsr setchr
	lda #$40
	jsr setchr
	inx
	inx
	sec
	lda prtbpa
	jsr setchr
	lda #$40
	jsr setchr
	dex
	sec
prtbpy	pla
	rts
prtbpv	lda prtbpa
	jsr setchr
	lda #$5D
	jsr setchr
	plp
	bcc prtbpz
	dey
	lda prtbpa
	jsr setchr
	lda #$5D
	jsr setchr
	iny
	iny
	sec
	lda prtbpa
	jsr setchr
	lda #$5D
	jsr setchr
	dey
	sec
prtbpz	pla
	rts
prtbpa	!byte 0

; Print all the board pieces.
prtbps	php
prbpy0	ldy #3
	lda #>ggraph
	sta prtbpp+2
prbpx0	ldx #8
	lda #<ggraph
	sta prtbpp+1
prtbp2	plp
prtbpp	lda ggraph
	jsr prtbpc
	inc prtbpp+1
	bcc prtbp3
	inx
prtbp3	inx
	php
prbpx1	cpx #31
	bcc prtbp2
	plp
	inc prtbpp+2
	bcc prtbp4
	iny
prtbp4	iny
	php
prbpy1	cpy #26
	bcc prbpx0
	plp
	rts

; Print the entire board.
prtbrd	clc
	jsr prtbrw
	jsr prtbcl
	jsr prtbbk
	jmp prtbps

; Print only changes to the board.
updbrd	clc
	jmp prtbps



; *************************
; Game Board Initialization
; *************************

; Initialize an odd row of the board.
iniodd	tya
	clc
	adc #>ggraph
	sta iniop1+2
	sta iniop2+2
	ldx #$00
iniolp	lda #$70	; XVOH
iniop1	sta ggraph,x
	inx
	lda #$D0	; O marker
iniop2	sta ggraph,x
	inx
	cpx #24
	bcc iniolp
	rts

; Initialize an even row of the board.
inievn	tya
	clc
	adc #>ggraph
	sta iniep1+2
	sta iniep2+2
	ldx #$00
inielp	lda #$90	; X marker
iniep1	sta ggraph,x
	inx
	lda #$60	; XHOV
iniep2	sta ggraph,x
	inx
	cpx #24
	bcc inielp
	rts

; Initialize an O edge (top or bottom row of the board).
inioed	tya
	clc
	adc #>ggraph
	sta inioe1+2
	sta inioe2+2
	ldx #$00
inioel	lda #$C3	; O edge, left and right
inioe1	sta ggraph,x
	inx
	lda #$D3	; O marker, left and right
inioe2	sta ggraph,x
	inx
	cpx #24
	bcc inioel
	rts

; Initialize an X edge (leftmost or rightmost column of the board).
inixed	ldy #>ggraph
	sty inixe1+2
	iny
	sty inixe2+2
	ldy #$00
inixel	lda #$8C	; X edge, up and down
inixe1	sta ggraph,x
	iny
	lda #$9C	; X marker, up and down
inixe2	sta ggraph,x
	iny
	cpy #24
	bcs inixex
	inc inixe1+2
	inc inixe1+2
	inc inixe2+2
	inc inixe2+2
	bcc inixel
inixex	rts

; Initialize the corners of the board.
inicnr	tya
	clc
	adc #>ggraph
	sta inicp1+2
	sta inicp2+2
	sta inicp7+2
	sta inicp8+2
	dec
	sta inicp3+2
	sta inicp4+2
	lda #>ggraph
	inc
	sta inicp5+2
	sta inicp6+2
	lda #$00	; dead spot
	sta ggraph
	sta ggraph,x
inicp1	sta ggraph
inicp2	sta ggraph,x
	lda #$98	; X marker, up
inicp3	sta ggraph
inicp4	sta ggraph,x
	lda #$94	; X marker, down
inicp5	sta ggraph
inicp6	sta ggraph,x
	phx
	dex
	lda #$D2	; O marker, left
	sta ggraph,x
inicp7	sta ggraph,x
	ldx #$01
	lda #$D1	; O marker, right
	sta ggraph,x
inicp8	sta ggraph,x
	plx
	rts

; Initialize the board.
inibrd	lda #22
	pha
	ldy #$00
iniblp	jsr iniodd
	iny
	jsr inievn
	iny
	cpy #24
	bcc iniblp
	ldy #$00
	jsr inioed
	ply
	jsr inioed
	phy
	ldx #$00
	jsr inixed
	plx
	jsr inixed
	txa
	tay
	jmp inicnr

; Configure the board according to board parameters.
cfgbrd	asl
	asl
	asl
	asl
	tax
	lda brdprm,x	; board size
	sta inibrd+1
	sta movemx+1
	sta movemy+1
	sta pathmx+1
	sta pathmy+1
	inc
	sta getamx+1
	sta getamy+1
	dec
	jsr getwcf
	inx
	lda brdprm,x	; carry bit setting
	sta prtbrd
	sta updbrd
	inx
	lda brdprm,x	; row number x0
	sta prbrx0+1
	inx
	lda brdprm,x	; row number x1
	sta prbrx1+1
	inx
	lda brdprm,x	; column letter x0
	sta prbcx0+1
	sta prbbx0+1
	sta prbpx0+1
	inx
	lda brdprm,x	; column letter x1
	sta prbcx1+1
	sta prbbx1+1
	sta prbpx1+1
	inx
	lda brdprm,x	; column letter y0
	sta prbcy0+1
	inx
	lda brdprm,x	; column letter y1
	sta prbcy1+1
	inx
	lda brdprm,x	; row number y0
	sta prbry0+1
	sta prbby0+1
	sta prbpy0+1
	inx
	lda brdprm,x	; row number y1
	sta prbry1+1
	sta prbby1+1
	sta prbpy1+1
	rts



; **************
; Gameplay Logic
; **************

; Make a move.
; In: Carry clear = X's move, carry set = O's move
; Out: Carry clear = move made, carry set = invalid move
mkmove	php
	cpx #1
	bcc moveng
	cpy #1
	bcc moveng
movemx	cpx #22
	bcs moveng
movemy	cpy #22
	bcs moveng
	tya
	adc #>ggraph
	sta movegp+2
	sta moveh1+2
	sta moveh2+2
	sta moveh3+2
	sta moveh4+2
	sta moveh5+2
	sta movev1+2
	inc
	sta movev2+2
	sta movev3+2
	dec
	dec
	sta movev4+2
	sta movev5+2
	inc
movegp	lda ggraph,x
	and #$F0
	cmp #$60
	beq mvxhov
	cmp #$70
	beq mvxvoh
moveng	plp
	sec
	rts

mvxhov	plp
	bcs moveov
movexh	lda #$A0
	bcc movehz
moveov	lda #$F0
	bcs movevt

mvxvoh	plp
	bcs moveoh
movexv	lda #$B0
	bcc movevt
moveoh	lda #$E0
	bcs movehz

movehz	ora #$03	; left and right
moveh1	sta ggraph,x
	inx
moveh2	lda ggraph,x
	ora #$02	; left
moveh3	sta ggraph,x
	dex
	dex
moveh4	lda ggraph,x
	ora #$01	; right
moveh5	sta ggraph,x
	inx
	clc
	rts

movevt	ora #$0C	; up and down
movev1	sta ggraph,x
movev2	lda ggraph,x
	ora #$08	; up
movev3	sta ggraph,x
movev4	lda ggraph,x
	ora #$04	; down
movev5	sta ggraph,x
	clc
	rts

; Clear the set of visited nodes.
clrvst	lda #>ggvstd
	sta clrvsp+2
	lda #$00
	ldy #$00
clrvs2	ldx #$00
clrvsp	sta ggvstd,x
	inx
	cpx #24
	bcc clrvsp
	inc clrvsp+2
	iny
	cpy #24
	bcc clrvs2
	rts

; Set a node as visited.
setvst	tya
	clc
	adc #>ggvstd
	sta setvsp+2
	lda #$FF
setvsp	sta ggvstd,x
	rts

; Check if a node is visited.
chkvst	tya
	clc
	adc #>ggvstd
	sta chkvsp+2
chkvsp	lda ggvstd,x
	beq chkvno
	sec
	rts
chkvno	clc
	rts

; Clear the visit queue.
clrvq	lda #<vqueue
	sta vqsplo
	sta vqeplo
	lda #>vqueue
	sta vqsphi
	sta vqephi
	rts

; Add A onto the visit queue.
pushvq	sta (vqeplo)
	inc vqeplo
	bne pushvx
	inc vqephi
pushvx	clc
	rts

; Add X, Y onto the visit queue.
phxyvq	txa
	jsr pushvq
	tya
	jsr pushvq
	rts

; Remove A from the visit queue.
pullvq	lda vqsplo
	cmp vqeplo
	bne pullok
	lda vqsphi
	cmp vqephi
	bne pullok
	sec
	rts
pullok	lda (vqsplo)
	inc vqsplo
	bne pullvx
	inc vqsphi
pullvx	clc
	rts

; Remove X, Y from the visit queue.
plxyvq	jsr pullvq
	bcs plxyvx
	tax
	jsr pullvq
	bcs plxyvx
	tay
plxyvx	rts

; Check if path exists.
; In: Carry clear for X, carry set for O.
; Out: Carry clear if path exists, carry set otherwise.
pathex	php
	; Clear visited list and visit queue.
	jsr clrvst
	jsr clrvq
	; Add starting node.
	plp
	bcs pathx2
	ldx #$00
	ldy #$01
	bcc pathx3
pathx2	ldx #$01
	ldy #$00
pathx3	php
	jsr phxyvq
	; Dequeue a node.
pathxl	jsr plxyvq
	; If empty, return no path exists.
	bcs pathno
	; If the end node, return path exists.
	plp
	bcs pathx4
	php
pathmx	cpx #22
	bcs pathsi
	bcc pathx5
pathx4	php
pathmy	cpy #22
	bcs pathsi
	; Set node visited.
pathx5	jsr setvst
	; Get adjacent nodes.
	tya
	clc
	adc #>ggraph
	sta pathxp+2
pathxp	lda ggraph,x
	; Add node above if connected and not visited.
	pha
	and #$08
	beq pathx6
	dey
	jsr chkvst
	bcs pathz6
	jsr phxyvq
pathz6	iny
pathx6	pla
	; Add node below if connected and not visited.
	pha
	and #$04
	beq pathx7
	iny
	jsr chkvst
	bcs pathz7
	jsr phxyvq
pathz7	dey
pathx7	pla
	; Add node to left if connected and not visited.
	pha
	and #$02
	beq pathx8
	dex
	jsr chkvst
	bcs pathz8
	jsr phxyvq
pathz8	inx
pathx8	pla
	; Add node to right if connected and not visited.
	pha
	and #$01
	beq pathx9
	inx
	jsr chkvst
	bcs pathz9
	jsr phxyvq
pathz9	dex
pathx9	pla
	; Continue processing queue.
	bra pathxl
	; Return no path exists.
pathno	plp
	sec
	rts
	; Return path exists.
pathsi	plp
	clc
	rts



; ****************
; Utilities for AI
; ****************

; Seed random number generator.
seed	lda via1ta
	sta seed0
	lda via1tb
	sta seed1
	lda via1tc
	sta seed2
	lda via1td
	sta seed3
	rts

; Linear congruential pseudo-random number generator.
; Calculate seed = 1664525 * seed + 1.
; http://6502.org/source/integers/random/random.html
rand	clc
	lda seed0
	sta tmp0
	adc #1
	sta seed0
	lda seed1
	sta tmp1
	adc #0
	sta seed1
	lda seed2
	sta tmp2
	adc tmp0
	sta seed2
	lda seed3
	sta tmp3
	adc tmp1
	sta seed3
	ldy #5
rand1	asl tmp0
	rol tmp1
	rol tmp2
	rol tmp3
	ldx rand4,y
	bpl rand2
	clc
	lda seed0
	adc tmp0
	sta seed0
	lda seed1
	adc tmp1
	sta seed1
	lda seed2
	adc tmp2
	sta seed2
	lda seed3
	adc tmp3
	sta seed3
	inx
	inx
rand2	clc
	beq rand3
	lda seed1
	adc tmp0
	sta seed1
rand3	lda seed2
	adc tmp0,x
	sta seed2
	lda seed3
	adc tmp1,x
	sta seed3
	dey
	bpl rand1
	rts
rand4	!byte $01,$01,$00,$FE,$FF,$01

; Linear congruential pseudo-random number generator.
; Get the next seed and obtain a 16-bit random number from it.
; In: mod = modulus
; Out: rnd = random number, 0 <= rnd < mod
; http://6502.org/source/integers/random/random.html
rand16	jsr rand
	lda #0
	sta rndhi
	sta rnd
	sta tmp0
	ldy #16
r16a	lsr modhi
	ror mod
	bcc r16b
	clc
	adc seed0
	tax
	lda tmp0
	adc seed1
	sta tmp0
	lda rnd
	adc seed2
	sta rnd
	lda rndhi
	adc seed3
	sta rndhi
	txa
r16b	ror rndhi
	ror rnd
	ror tmp0
	ror
	dey
	bne r16a
	rts

; Shuffle 16-bit values in memory.
shuf16	sec
	lda sheplo
	sbc shsplo
	sta shlnlo
	lda shephi
	sbc shsphi
	sta shlnhi
	; while shln <> 0
s16a	lda shlnlo
	bne s16b
	lda shlnhi
	bne s16b
	rts
	; rnd = rand16(shln) & $FFFE
s16b	lda shlnlo
	sta mod
	lda shlnhi
	sta modhi
	jsr rand16
	lda #$FE
	and rnd
	sta rnd
	; shln -= 2
	sec
	lda shlnlo
	sbc #$02
	sta shlnlo
	lda shlnhi
	sbc #$00
	sta shlnhi
	; swap shsp[shln] and shsp[rnd]
	clc
	lda shsplo
	adc shlnlo
	sta mod
	lda shsphi
	adc shlnhi
	sta modhi
	clc
	lda shsplo
	adc rnd
	sta rnd
	lda shsphi
	adc rndhi
	sta rndhi
	ldy #$00
	lda (mod),y
	sta tmp0
	lda (rnd),y
	sta (mod),y
	lda tmp0
	sta (rnd),y
	iny
	lda (mod),y
	sta tmp1
	lda (rnd),y
	sta (mod),y
	lda tmp1
	sta (rnd),y
	bra s16a



; ******************
; Computer Player AI
; ******************

; Initialize distance and previous node arrays.
clrdst	lda #>ggdstl
	sta clrdlx+2
	lda #>ggdsth
	sta clrdlx+5
	lda #>ggprvx
	sta clrdlx+8
	lda #>ggprvy
	sta clrdlx+11
	lda #$FF
	ldy #$00
clrdly	ldx #$00
clrdlx	sta ggdstl,x
	sta ggdsth,x
	sta ggprvx,x
	sta ggprvy,x
	inx
	cpx #24
	bcc clrdlx
	iny
	cpy #24
	bcs clrdex
	inc clrdlx+2
	inc clrdlx+5
	inc clrdlx+8
	inc clrdlx+11
	bcc clrdly
clrdex	ldx #$00	; virtual nodes
clrdlz	sta xsdstl,x
	inx
	cpx #16
	bcc clrdlz
	rts

; Get distance and previous node.
getdst	cpx #$75	; X start (virtual node)
	beq getdxs
	cpx #$7E	; X end (virtual node)
	beq getdxe
	cpx #$F5	; O start (virtual node)
	beq getdos
	cpx #$FE	; O end (virtual node)
	beq getdoe
	tya
	clc
	adc #>ggdstl
	sta getdp1+2
getdp1	lda ggdstl,x
	sta rdstl
	tya
	clc
	adc #>ggdsth
	sta getdp2+2
getdp2	lda ggdsth,x
	sta rdsth
	tya
	clc
	adc #>ggprvx
	sta getdp3+2
getdp3	lda ggprvx,x
	sta rprvx
	tya
	clc
	adc #>ggprvy
	sta getdp4+2
getdp4	lda ggprvy,x
	sta rprvy
	rts
getdxs	phx
	ldx #$00
	bra getdvn
getdxe	phx
	ldx #$04
	bra getdvn
getdos	phx
	ldx #$08
	bra getdvn
getdoe	phx
	ldx #$0C
getdvn	lda xsdstl,x
	sta rdstl
	lda xsdsth,x
	sta rdsth
	lda xsprvx,x
	sta rprvx
	lda xsprvy,x
	sta rprvy
	plx
	rts

; Set distance and previous node.
setdst	cpx #$75	; X start (virtual node)
	beq setdxs
	cpx #$7E	; X end (virtual node)
	beq setdxe
	cpx #$F5	; O start (virtual node)
	beq setdos
	cpx #$FE	; O end (virtual node)
	beq setdoe
	tya
	clc
	adc #>ggdstl
	sta setdp1+2
	lda rdstl
setdp1	sta ggdstl,x
	tya
	clc
	adc #>ggdsth
	sta setdp2+2
	lda rdsth
setdp2	sta ggdsth,x
	tya
	clc
	adc #>ggprvx
	sta setdp3+2
	lda rprvx
setdp3	sta ggprvx,x
	tya
	clc
	adc #>ggprvy
	sta setdp4+2
	lda rprvy
setdp4	sta ggprvy,x
	rts
setdxs	phx
	ldx #$00
	bra setdvn
setdxe	phx
	ldx #$04
	bra setdvn
setdos	phx
	ldx #$08
	bra setdvn
setdoe	phx
	ldx #$0C
setdvn	lda rdstl
	sta xsdstl,x
	lda rdsth
	sta xsdsth,x
	lda rprvx
	sta xsprvx,x
	lda rprvy
	sta xsprvy,x
	plx
	rts

xsdstl	!byte $FF
xsdsth  !byte $FF
xsprvx	!byte $FF
xsprvy	!byte $FF
xedstl	!byte $FF
xedsth  !byte $FF
xeprvx	!byte $FF
xeprvy	!byte $FF
osdstl	!byte $FF
osdsth  !byte $FF
osprvx	!byte $FF
osprvy	!byte $FF
oedstl	!byte $FF
oedsth  !byte $FF
oeprvx	!byte $FF
oeprvy	!byte $FF
rdstl	!byte $FF
rdsth	!byte $FF
rprvx	!byte $FF
rprvy	!byte $FF

; Like clrvst but can handle virtual nodes.
clrvvs	lda #$00	; virtual nodes
	sta xsvstd
	sta xevstd
	sta osvstd
	sta oevstd
	jmp clrvst

; Like setvst but can handle virtual nodes.
setvvs	cpx #$75	; X start (virtual node)
	beq setvxs
	cpx #$7E	; X end (virtual node)
	beq setvxe
	cpx #$F5	; O start (virtual node)
	beq setvos
	cpx #$FE	; O end (virtual node)
	beq setvoe
	jmp setvst
setvxs	lda #$FF
	sta xsvstd
	rts
setvxe	lda #$FF
	sta xevstd
	rts
setvos	lda #$FF
	sta osvstd
	rts
setvoe	lda #$FF
	sta oevstd
	rts

; Like chkvst but can handle virtual nodes.
chkvvs	cpx #$75	; X start (virtual node)
	beq chkvxs
	cpx #$7E	; X end (virtual node)
	beq chkvxe
	cpx #$F5	; O start (virtual node)
	beq chkvos
	cpx #$FE	; O end (virtual node)
	beq chkvoe
	jmp chkvst
chkvxs	lda xsvstd
	beq chkvv1
	bne chkvv2
chkvxe	lda xevstd
	beq chkvv1
	bne chkvv2
chkvos	lda osvstd
	beq chkvv1
	bne chkvv2
chkvoe	lda oevstd
	beq chkvv1
	bne chkvv2
chkvv1	clc
	rts
chkvv2	sec
	rts

xsvstd	!byte $00
xevstd	!byte $00
osvstd	!byte $00
oevstd	!byte $00

; Put all the nodes in the visit queue.
getall	jsr clrvq
	; X start (virtual node)
	lda #$75
	jsr pushvq
	jsr pushvq
	; X end (virtual node)
	lda #$7E
	jsr pushvq
	jsr pushvq
	; O start (virtual node)
	lda #$F5
	jsr pushvq
	jsr pushvq
	; O end (virtual node)
	lda #$FE
	jsr pushvq
	jsr pushvq
	; all X and O markers
	ldy #$01
getany	ldx #$00
getanx	txa
	jsr pushvq
	tya
	jsr pushvq
	jsr pushvq
	txa
	jsr pushvq
	inx
	inx
getamx	cpx #23
	bcc getanx
	iny
	iny
getamy	cpy #23
	bcc getany
	; shuffle the visit queue
	lda vqsplo
	sta shsplo
	lda vqsphi
	sta shsphi
	lda vqeplo
	sta sheplo
	lda vqephi
	sta shephi
	jmp shuf16

; Find unvisited node in visit queue with smallest distance.
; Returns with carry set iff all nodes have been visited.
findu	lda #$FF
	sta currx
	sta curry
	sta cdstl
	sta cdsth
	; shep = vqep; shln = vqep - vqsp;
	sec
	lda vqeplo
	sta sheplo
	sbc vqsplo
	sta shlnlo
	lda vqephi
	sta shephi
	sbc vqsphi
	sta shlnhi
	; while shln <> 0
findu1	lda shlnlo
	bne findu2
	lda shlnhi
	bne findu2
	ldx currx
	ldy curry
	lda currx
	and curry
	and cdstl
	and cdsth
	cmp #$FF
	beq findux
	clc
	rts
findux	sec
	rts
	; shln -= 2
findu2	sec
	lda shlnlo
	sbc #$02
	sta shlnlo
	lda shlnhi
	sbc #$00
	sta shlnhi
	; y = *--shep; x = *--shep;
	sec
	lda sheplo
	sbc #$01
	sta sheplo
	lda shephi
	sbc #$00
	sta shephi
	lda (sheplo)
	tay
	sec
	lda sheplo
	sbc #$01
	sta sheplo
	lda shephi
	sbc #$00
	sta shephi
	lda (sheplo)
	tax
	; if visited(x,y) continue;
	jsr chkvvs
	bcs findu1
	; if distance(x,y) >= cdst continue;
	jsr getdst
	lda rdsth
	cmp cdsth
	bcc findu3	; rdst < cdst
	bne findu1	; rdst > cdst
	lda rdstl
	cmp cdstl
	bcs findu1	; rdst >= cdst
	; currx = x; curry = y; cdst = rdst;
findu3	stx currx
	sty curry
	lda rdstl
	sta cdstl
	lda rdsth
	sta cdsth
	bra findu1
currx	!byte $FF
curry	!byte $FF
cdstl	!byte $FF
cdsth	!byte $FF

; Configure getwan.
getwcf	sta getwxe+1
	sta getwoe+1
	sta getwxm+1
	sta getwom+1
	sta getwxc+1
	sta getwoc+1
	dec
	sta gwupex+1
	sta gwltex+1
	inc
	rts

; Get all nodes for X/O start/end.
; Branched into from getwan. Never called directly.
getwxs	lda #0
	bra getwxi
getwxe	lda #22
	bra getwxi
getwos	lda #0
	bra getwoi
getwoe	lda #22
	bra getwoi
getwxi	ldx #0
	ldy #1
getwxl	sta wanx,x
	pha
	tya
	sta wany,x
	lda #0
	sta wanw,x
	pla
	inx
	iny
	iny
getwxm	cpy #22
	bcc getwxl
	stx wancnt
	rts
getwoi	ldx #0
	ldy #1
getwol	sta wany,x
	pha
	tya
	sta wanx,x
	lda #0
	sta wanw,x
	pla
	inx
	iny
	iny
getwom	cpy #22
	bcc getwol
	stx wancnt
	rts

; Get all adjacent nodes and their weights.
getwan	cpx #$75
	beq getwxs
	cpx #$7E
	beq getwxe
	cpx #$F5
	beq getwos
	cpx #$FE
	beq getwoe
	; Initialize returned list and get node.
	lda #0
	sta wancnt
	jsr getwgn
	; Get weight of node above.
	; Skip if there is no node above.
	cpy #2
	bcc gwupex
	phy
	phx
	pha
	; If connected, add with weight of 0.
	and #$08
	beq gwupnc
	dey
	dey
	lda #0
	jsr getwph
	bra gwuppl
	; If blocked, skip.
gwupnc	dey
	jsr getwgn
	bmi gwuppl
	; Otherwise, add with weight of 1.
	dey
	lda #1
	jsr getwph
gwuppl	pla
	plx
	ply
	; Get weight of node below.
	; Skip if there is no node below.
gwupex	cpy #21
	bcs gwdnex
	phy
	phx
	pha
	; If connected, add with weight of 0.
	and #$04
	beq gwdnnc
	iny
	iny
	lda #0
	jsr getwph
	bra gwdnpl
	; If blocked, skip.
gwdnnc	iny
	jsr getwgn
	bmi gwdnpl
	; Otherwise, add with weight of 1.
	iny
	lda #1
	jsr getwph
gwdnpl	pla
	plx
	ply
	; Get weight of node to the left.
	; Skip if there is no node to the left.
gwdnex	cpx #2
	bcc gwltex
	phy
	phx
	pha
	; If connected, add with weight of 0.
	and #$02
	beq gwltnc
	dex
	dex
	lda #0
	jsr getwph
	bra gwltpl
	; If blocked, skip.
gwltnc	dex
	jsr getwgn
	bmi gwltpl
	; Otherwise, add with weight of 1.
	dex
	lda #1
	jsr getwph
gwltpl	pla
	plx
	ply
	; Get weight of node to the right.
	; Skip if there is no node to the right.
gwltex	cpx #21
	bcs gwrtex
	phy
	phx
	pha
	; If connected, add with weight of 0.
	and #$01
	beq gwrtnc
	inx
	inx
	lda #0
	jsr getwph
	bra gwrtpl
	; If blocked, skip.
gwrtnc	inx
	jsr getwgn
	bmi gwrtpl
	; Otherwise, add with weight of 1.
	inx
	lda #1
	jsr getwph
gwrtpl	pla
	plx
	ply
	; If node is on an edge add the X/O start/end node.
gwrtex	cpx #0
	beq gwadxs
getwxc	cpx #22
	beq gwadxe
	cpy #0
	beq gwados
getwoc	cpy #22
	beq gwadoe
	ldx wancnt
	rts
gwadxs	lda #$75
	bra gwadd
gwadxe	lda #$7E
	bra gwadd
gwados	lda #$F5
	bra gwadd
gwadoe	lda #$FE
gwadd	ldx wancnt
	sta wanx,x
	sta wany,x
	lda #0
	sta wanw,x
	inx
	stx wancnt
	rts

; Get a node in the graph.
; X = x coordinate, Y = y coordinate.
; Returns with node in A and X, Y unchanged.
getwgn	tya
	clc
	adc #>ggraph
	sta getwgp+2
getwgp	lda ggraph,x
	rts

; Add a node to the returned weighted adjacent nodes.
; A = weight, X = x coordinate, Y = y coordinate.
; Returns with A, X, Y unchanged.
getwph	phx
	pha
	txa
	ldx wancnt
	sta wanx,x
	tya
	sta wany,x
	pla
	sta wanw,x
	inx
	stx wancnt
	plx
	rts

wancnt	!byte 0
wanx	!byte 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
wany	!byte 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
wanw	!byte 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0

; Find shortest path from X, Y to... everywhere, really.
shpath	phy
	phx
	jsr clrdst
	; dist[source] := 0
	lda #$00
	sta rdstl
	sta rdsth
	lda #$FF
	sta rprvx
	sta rprvy
	plx
	ply
	jsr setdst
	; Q := the set of all nodes in Graph
	jsr clrvvs
	jsr getall
	; while Q is not empty:
	; u := node in Q with smallest dist[]
shpth1	jsr findu	; cdst = dist[u]
	bcs shpthx
	; remove u from Q
	jsr setvvs
	; for each neighbor v of u:
	jsr getwan
shpth2	ldx wancnt
	beq shpth1
	dex
	stx wancnt
	; (where v has not yet been removed from Q)
	lda wany,x
	tay
	lda wanx,x
	tax
	jsr chkvvs
	bcs shpth2
	jsr getdst	; rdst = dist[v]
	; alt := dist[u] + dist_between(u, v)
	clc
	lda cdstl
	ldx wancnt
	adc wanw,x
	sta shaltl
	lda cdsth
	adc #$00
	sta shalth
	; if alt < dist[v]
	cmp rdsth
	bcc shpth3	; shalt < rdst (alt < dist[v])
	bne shpth2	; shalt > rdst (alt > dist[v])
	lda shaltl
	cmp rdstl
	bcc shpth4	; shalt < rdst (alt < dist[v])
	bcs shpth2	; shalt >= rdst (alt >= dist[v])
	; dist[v] := alt, previous[v] := u
shpth3	lda shaltl
shpth4	sta rdstl
	lda shalth
	sta rdsth
	lda currx
	sta rprvx
	lda curry
	sta rprvy
	ldx wancnt
	lda wany,x
	tay
	lda wanx,x
	tax
	jsr setdst
	bra shpth2
shpthx	rts
shaltl	!byte $00
shalth	!byte $00

; Get shortest path found by shpath ending at X, Y.
; Returns path in $(A)00, length in A, start node in X, Y.
pathto	sta shsphi
	lda #$00
	sta shsplo
	sta wancnt
	; Add x, y to path.
pathph	txa
	sta (shsplo)
	inc shsplo
	bne pathpx
	inc shsphi
pathpx	tya
	sta (shsplo)
	inc shsplo
	bne pathpy
	inc shsphi
pathpy	inc wancnt
	; Get previous node. If no previous node, exit.
	jsr getdst
	lda rprvx
	and rprvy
	cmp #$FF
	beq pathtx
	ldx rprvx
	ldy rprvy
	bra pathph
pathtx	lda wancnt
	rts

; Get node between two points on a path (address in vqsplo).
; If either point is a virtual node, returns with carry set.
avgpth	ldy #$00
	lda (vqsplo),y
	cmp #$70
	bcs avgptx
	iny
	iny
	lda (vqsplo),y
	cmp #$70
	bcs avgptx
	dey
	dey
	adc (vqsplo),y
	ror
	tax
	iny
	lda (vqsplo),y
	cmp #$70
	bcs avgptx
	iny
	iny
	lda (vqsplo),y
	cmp #$70
	bcs avgptx
	dey
	dey
	adc (vqsplo),y
	ror
	tay
	clc
avgptx	rts

; Check if X, Y is a valid move.
chkvld	tya
	clc
	adc #>ggraph
	sta chkvlp+2
chkvlp	lda ggraph,x
	and #$F0
	cmp #$60
	beq chkvlk
	cmp #$70
	beq chkvlk
	sec
	rts
chkvlk	clc
	rts

; Convert a path to a (randomized) list of valid moves.
; Path length in A, path in $(X)00, moves in $(Y)00.
; X and Y can be the same.
pthmvs	sty shsphi
	sty shephi
	ldy #$00
	sty shsplo
	sty sheplo
	sty wancnt
	sty vqsplo
	stx vqsphi
	pha
	asl
	sta vqeplo
	bcc pthmv1
	inx
pthmv1	stx vqephi
pthmlp	plx
	cpx #$02
	bcc pthmvx
	dex
	phx
	jsr avgpth
	bcs pthmlc
	jsr chkvld
	bcs pthmlc
	txa
	sta (sheplo)
	inc sheplo
	bne pthmv2
	inc shephi
pthmv2	tya
	sta (sheplo)
	inc sheplo
	bne pthmv3
	inc shephi
pthmv3	inc wancnt
	sec
pthmlc	lda vqsplo
	adc #$01
	sta vqsplo
	lda vqsphi
	adc #$00
	sta vqsphi
	bra pthmlp
pthmvx	jsr shuf16
	lda wancnt
	rts

; Put valid X moves in pqueue.
pvxmpq	ldx #$75
	ldy #$75
	jsr shpath
	lda #>pqueue
	ldx #$7E
	ldy #$7E
	jsr pathto
	ldx #>pqueue
	ldy #>pqueue
	jmp pthmvs

; Put valid O moves in pqueue.
pvompq	ldx #$F5
	ldy #$F5
	jsr shpath
	lda #>pqueue
	ldx #$FE
	ldy #$FE
	jsr pathto
	ldx #>pqueue
	ldy #>pqueue
	jmp pthmvs

; Put valid X moves in vqueue.
pvxmvq	ldx #$75
	ldy #$75
	jsr shpath
	lda #>vqueue
	ldx #$7E
	ldy #$7E
	jsr pathto
	ldx #>vqueue
	ldy #>vqueue
	jmp pthmvs

; Put valid O moves in vqueue.
pvomvq	ldx #$F5
	ldy #$F5
	jsr shpath
	lda #>vqueue
	ldx #$FE
	ldy #$FE
	jsr pathto
	ldx #>vqueue
	ldy #>vqueue
	jmp pthmvs

; Get AI move.
gtaimv	lda gstate
	ror
	bcc aiisx
	; AI is O
	; put valid X moves in pqueue
aiiso	jsr pvxmpq
	sta umvcnt
	; put valid O moves in vqueue
	jsr pvomvq
	sta cmvcnt
	bra rtaimv
	; AI is X
	; put valid O moves in pqueue
aiisx	jsr pvompq
	sta umvcnt
	; put valid X moves in vqueue
	jsr pvxmvq
	sta cmvcnt
	; vqueue contains computer's best moves
	; pqueue contains player's best moves
	; if umvcnt >= cmvcnt return computer move
	; if umvcnt < cmvcnt return user move (to block)
rtaimv	lda umvcnt
	cmp cmvcnt
	bcc retumv
retcmv	ldx vqueue+0
	ldy vqueue+1
	rts
retumv	ldx pqueue+0
	ldy pqueue+1
	rts
cmvcnt	!byte $00
umvcnt	!byte $00



; *******************
; Static Data Section
; *******************

title	!text "BRIDGET", 0

movevc	!byte $21, 13, <movexf, >movexf
	!byte $51, 13, <moveof, >moveof
	!byte $21, 15, <movexm, >movexm
	!byte $51, 15, <moveom, >moveom
	!byte $21, 16, <movexl, >movexl
	!byte $51, 16, <moveol, >moveol

movexf	!text "X MOVES FIRST", 0
moveof	!text "O MOVES FIRST", 0
movexm	!text "X'S  TURN", 0
moveom	!text "O'S  TURN", 0
movexl	!text "X WINS!", 0
moveol	!text "O WINS!", 0

helpvc	!byte 1, 2, <help00, >help00
	!byte 1, 3, <help01, >help01
	!byte 1, 4, <help02, >help02
	!byte 1, 5, <help03, >help03
	!byte 1, 6, <help04, >help04
	!byte 1, 7, <help05, >help05
	!byte 1, 8, <help06, >help06
	!byte 1, 9, <help07, >help07
	!byte 1, 10, <help08, >help08
	!byte 1, 12, <help09, >help09
	!byte 1, 13, <help10, >help10
	!byte 1, 14, <help11, >help11
	!byte 4, 16, <help12, >help12
	!byte 4, 17, <help13, >help13
	!byte 4, 18, <help14, >help14
	!byte 4, 19, <help15, >help15
	!byte 4, 20, <help16, >help16
	!byte 4, 21, <help17, >help17
	!byte 4, 22, <help18, >help18
	!byte 4, 23, <help19, >help19
	!byte 4, 24, <help20, >help20
	!byte 4, 26, <help21, >help21
	!byte 0, 0

help00	!text "IN  THIS GAME,  ONE  PLAYER  ATTEMPTS", 0
help01	!text "TO FORM  A CONNECTED BRIDGE  FROM THE", 0
help02	!text "TOP  TO THE BOTTOM  OF  THE BOARD  BY", 0
help03	!text "CONNECTING HORIZONTALLY OR VERTICALLY", 0
help04	!text "ADJACENT  GREEN  SQUARES.  THE  OTHER", 0
help05	!text "PLAYER ATTEMPTS TO FORM A BRIDGE FROM", 0
help06	!text "THE LEFT  TO THE RIGHT  BY CONNECTING", 0
help07	!text "RED SQUARES. THE FIRST PLAYER TO FORM", 0
help08	!text "A BRIDGE WINS.", 0
help09	!text "ENTER A COLUMN LETTER  AND ROW NUMBER", 0
help10	!text "OF AN EMPTY SPACE TO MAKE A MOVE. OR,", 0
help11	!text "ENTER ONE OF THE FOLLOWING COMMANDS:", 0
help12	!text "1: 1-PLAYER GAME (VS COMPUTER)", 0
help13	!text "2: 2-PLAYER GAME (VS PLAYER)", 0
help14	!text "3: BOARD SIZE: SMALL", 0
help15	!text "4: BOARD SIZE: MEDIUM", 0
help16	!text "5: BOARD SIZE: LARGE", 0
help17	!text "6: BOARD SIZE: HUGE", 0
help18	!text "7: BOARD SIZE: GIGANTIC", 0
help19	!text "8: HELP (THIS SCREEN)", 0
help20	!text "9: QUIT", 0
help21	!text "(C) 2020-2023 KREATIVE SOFTWARE", 0

prmcmd	!text "ENTER COMMAND: ", 0
prmcol	!text "ENTER COLUMN LETTER OR COMMAND: ", 0
prmrow	!text "ENTER ROW NUMBER (ESC TO CANCEL): A", 0
prminv	!text "INVALID MOVE. TRY AGAIN? (Y/N): ", 0
prmai	!text "WAITING FOR COMPUTER'S MOVE...", 0
prmn1p	!text "START NEW 1-PLAYER GAME? (Y/N): ", 0
prmn2p	!text "START NEW 2-PLAYER GAME? (Y/N): ", 0
prmsml	!text "SWITCH TO SMALL BOARD? (Y/N): ", 0
prmmed	!text "SWITCH TO MEDIUM BOARD? (Y/N): ", 0
prmlrg	!text "SWITCH TO LARGE BOARD? (Y/N): ", 0
prmhug	!text "SWITCH TO HUGE BOARD? (Y/N): ", 0
prmgig	!text "SWITCH TO GIGANTIC BOARD? (Y/N): ", 0
prmex	!text "QUIT? (Y/N): ", 0

statl1	!text $B1,"1P ",$B2,"2P ",$B3,"S ",$B4,"M ",$B5,"L ",$B6,"XL ",$B7,"XXL ",$B8,"CONT ",$B9,"QUIT",0
statl2	!text $B1,"1P ",$B2,"2P ",$B3,"S ",$B4,"M ",$B5,"L ",$B6,"XL ",$B7,"XXL ",$B8,"HELP ",$B9,"QUIT",0

	;     sz  cry rnx0rnx1clx0clx1cly0cly1rny0rny1 reserved-for-l8r
brdprm	!byte  6, $38, 10, 27, 13, 26,  6, 22,  8, 21, 0, 0, 0, 0, 0, 0
	!byte 10, $38,  6, 31,  9, 30,  2, 26,  4, 25, 0, 0, 0, 0, 0, 0
	!byte 14, $18, 10, 27, 12, 27,  6, 22,  7, 22, 0, 0, 0, 0, 0, 0
	!byte 18, $18,  8, 29, 10, 29,  4, 24,  5, 24, 0, 0, 0, 0, 0, 0
	!byte 22, $18,  6, 31,  8, 31,  2, 26,  3, 26, 0, 0, 0, 0, 0, 0



; ***********
; END OF FILE
; ***********
