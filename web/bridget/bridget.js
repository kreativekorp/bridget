(function($,window,document){

var xsTurn = false;
var osTurn = false;
var aiPlayer = false;
var boardSize = 0;
var boardGraph = {};

var connectBridge = function(e, x, y, horiz) {
	if (boardGraph[x + '-' + y]) return false;
	if (horiz) {
		var n1 = (x-1) + '-' + y;
		var n2 = (x-(-1)) + '-' + y;
		boardGraph[n1][n2] = true;
		boardGraph[n2][n1] = true;
		e.addClass('h');
	} else {
		var n1 = x + '-' + (y-1);
		var n2 = x + '-' + (y-(-1));
		boardGraph[n1][n2] = true;
		boardGraph[n2][n1] = true;
		e.addClass('v');
	}
	boardGraph[x + '-' + y] = true;
	return true;
};

var pathExists = function(n1, n2) {
	var visited = {};
	var toVisit = [n1];
	while (toVisit.length) {
		var n = toVisit.shift();
		if (n === n2) return true;
		visited[n] = true;
		$.each(boardGraph[n], function(k,v) {
			if (v && !visited[k]) toVisit.push(k);
		});
	}
	return false;
};

var startNextTurn = function() {
	var status = $('.status');
	if (xsTurn) {
		xsTurn = false;
		if (pathExists('xstart', 'xend')) {
			osTurn = false;
			status.text('X Wins!');
			status.addClass('xstatus').removeClass('ostatus');
			return false;
		} else {
			osTurn = true;
			status.text('O\'s Turn');
			status.addClass('ostatus').removeClass('xstatus');
			return true;
		}
	}
	if (osTurn) {
		osTurn = false;
		if (pathExists('ostart', 'oend')) {
			xsTurn = false;
			status.text('O Wins!');
			status.addClass('ostatus').removeClass('xstatus');
			return false;
		} else {
			xsTurn = true;
			status.text('X\'s Turn');
			status.addClass('xstatus').removeClass('ostatus');
			return true;
		}
	}
};

var shuffle = function(a) {
	var ci = a.length;
	while (ci) {
		var ri = Math.floor(Math.random() * ci); ci--;
		var tmp = a[ci]; a[ci] = a[ri]; a[ri] = tmp;
	}
	return a;
};

var getAllNodes = function() {
	var nodes = ['xstart', 'xend', 'ostart', 'oend'];
	var n = boardSize * 2 + 3;
	for (var j = 1; j < n; j += 2) {
		for (var i = 0; i < n; i += 2) {
			nodes.push(i + '-' + j);
			nodes.push(j + '-' + i);
		}
	}
	return shuffle(nodes);
};

var getWeightedAdjacentNodes = function(node) {
	var weights = {};
	var xy = node.split('-');
	if (xy.length === 2) {
		var n = boardSize * 2 + 3;
		if ((xy[0]-2)   >= 0 && !boardGraph[(xy[0]-1)    + '-' + xy[1]]) weights[(xy[0]-2)    + '-' + xy[1]] = 65536;
		if ((xy[0]-(-2)) < n && !boardGraph[(xy[0]-(-1)) + '-' + xy[1]]) weights[(xy[0]-(-2)) + '-' + xy[1]] = 65536;
		if ((xy[1]-2)   >= 0 && !boardGraph[xy[0] + '-' + (xy[1]-1)   ]) weights[xy[0] + '-' + (xy[1]-2)   ] = 65536;
		if ((xy[1]-(-2)) < n && !boardGraph[xy[0] + '-' + (xy[1]-(-1))]) weights[xy[0] + '-' + (xy[1]-(-2))] = 65536;
	}
	$.each(boardGraph[node], function(k,v) { if (v) weights[k] = 1; });
	return shuffle($.map(weights, function(v,k) { return [[k,v]]; }));
};

var getShortestPath = function(n1, n2) {
	/* dist[source] := 0 */
	var dist = {}; dist[n1] = 0;
	var prev = {};
	/* Q := the set of all nodes in Graph */
	var q = getAllNodes();
	var r = {};
	/* while Q is not empty: */
	while (q.length) {
		/* u := node in Q with smallest dist[] */
		var u = -1;
		var d = Infinity;
		for (var i = 0; i < q.length; i++) {
			if (dist[q[i]] !== undefined) {
				if (u < 0 || dist[q[i]] < d) {
					u = i;
					d = dist[q[i]];
				}
			}
		}
		/* remove u from Q */
		u = q.splice(u,1)[0];
		r[u] = true;
		/* for each neighbor v of u: */
		var w = getWeightedAdjacentNodes(u);
		for (var i = 0; i < w.length; i++) {
			var v = w[i][0];
			/* where v has not yet been removed from Q: */
			if (!r[v]) {
				/* alt := dist[u] + dist_between(u, v) */
				var alt = d + w[i][1];
				/* if alt < dist[v]: */
				if (dist[v] === undefined || alt < dist[v]) {
					/* dist[v] := alt */
					/* previous[v] := u */
					dist[v] = alt;
					prev[v] = u;
				}
			}
		}
	}
	var path = [];
	while (n2) {
		path.unshift(n2);
		n2 = prev[n2];
	}
	return path;
};

var makeAIMove = function() {
	var xpath = getShortestPath('xstart', 'xend');
	var opath = getShortestPath('ostart', 'oend');
	var cpath = xsTurn ? xpath : osTurn ? opath : [];
	var upath = xsTurn ? opath : osTurn ? xpath : [];
	var cmoves = [];
	var umoves = [];
	for (var i = 2; i < cpath.length-1; i++) {
		var xy1 = cpath[i-1].split('-');
		var xy2 = cpath[i].split('-');
		var x = (xy1[0]-(-xy2[0]))/2;
		var y = (xy1[1]-(-xy2[1]))/2;
		if (!boardGraph[x+'-'+y]) cmoves.push([x,y]);
	}
	for (var i = 2; i < upath.length-1; i++) {
		var xy1 = upath[i-1].split('-');
		var xy2 = upath[i].split('-');
		var x = (xy1[0]-(-xy2[0]))/2;
		var y = (xy1[1]-(-xy2[1]))/2;
		if (!boardGraph[x+'-'+y]) umoves.push([x,y]);
	}

	var x, y;
	if (cmoves.length <= umoves.length) {
		var i = Math.floor(Math.random() * cmoves.length);
		x = cmoves[i][0];
		y = cmoves[i][1];
	} else {
		var i = Math.floor(Math.random() * umoves.length);
		x = umoves[i][0];
		y = umoves[i][1];
	}

	var s = ((x & 1) ? 'xhov' : 'xvoh');
	var e = $('#' + s + '-' + x + '-' + y);
	if (xsTurn) connectBridge(e, x, y, s === 'xhov');
	if (osTurn) connectBridge(e, x, y, s === 'xvoh');
};

var clickBridge = function() {
	var e = $(this);
	var d = e.attr('id').split('-');
	var xMoved = xsTurn && connectBridge(e, d[1], d[2], d[0] === 'xhov');
	var oMoved = osTurn && connectBridge(e, d[1], d[2], d[0] === 'xvoh');
	if ((xMoved || oMoved) && startNextTurn() && aiPlayer) {
		window.setTimeout(function() {
			makeAIMove();
			startNextTurn();
		}, 10);
	}
};

var makeBoard = function(b) {
	var board = $('.board');
	var bl = 50.0/(b*3+4);
	var sl = 100.0/(b*3+4);
	var ml = 150.0/(b*3+4);
	var ll = 200.0/(b*3+4);
	var n = b*2+3;

	boardSize = b;
	boardGraph = {'xstart': {}, 'xend': {}, 'ostart': {}, 'oend': {}};
	for (var j = 1; j < n; j += 2) {
		for (var i = 0; i < n; i += 2) {
			boardGraph[i + '-' + j] = {};
			boardGraph[j + '-' + i] = {};
		}
	}
	for (var i = 1; i < n; i += 2) {
		var xs = 0 + '-' + i;
		var xe = (n-1) + '-' + i;
		var os = i + '-' + 0;
		var oe = i + '-' + (n-1);
		boardGraph['xstart'][xs] = true;
		boardGraph[xs]['xstart'] = true;
		boardGraph['xend'][xe] = true;
		boardGraph[xe]['xend'] = true;
		boardGraph['ostart'][os] = true;
		boardGraph[os]['ostart'] = true;
		boardGraph['oend'][oe] = true;
		boardGraph[oe]['oend'] = true;
	}

	board.empty();
	for (var j = 1; j < n; j += 2) {
		for (var i = 0; i < n; i += 2) {
			var x = $('<div/>').addClass('x');
			var o = $('<div/>').addClass('o');
			x.css('width', sl+'%').css('height', sl+'%');
			o.css('width', sl+'%').css('height', sl+'%');
			x.css('left', (i*ml)+'%').css('top', (j*ml)+'%');
			o.css('left', (j*ml)+'%').css('top', (i*ml)+'%');
			board.append(x);
			board.append(o);
		}
	}
	for (var j = 1; j < n-1; j += 2) {
		for (var i = 1; i < n-1; i += 2) {
			var xhov = $('<div/>').addClass('xhov');
			xhov.css('width', ll+'%').css('height', ll+'%');
			xhov.css('left', (i*ml-bl)+'%').css('top', (j*ml-bl)+'%');
			xhov.attr('id', 'xhov-'+i+'-'+j);
			xhov.click(clickBridge);
			board.append(xhov);
		}
	}
	for (var j = 2; j < n-2; j += 2) {
		for (var i = 2; i < n-2; i += 2) {
			var xvoh = $('<div/>').addClass('xvoh');
			xvoh.css('width', ll+'%').css('height', ll+'%');
			xvoh.css('left', (i*ml-bl)+'%').css('top', (j*ml-bl)+'%');
			xvoh.attr('id', 'xvoh-'+i+'-'+j);
			xvoh.click(clickBridge);
			board.append(xvoh);
		}
	}
};

var startGame = function(b, ai) {
	xsTurn = (Math.random() < 0.5);
	osTurn = !xsTurn;
	aiPlayer = !!ai;
	makeBoard(b);
	if (xsTurn) $('.status').text('X Moves First').addClass('xstatus').removeClass('ostatus');
	if (osTurn) $('.status').text('O Moves First').addClass('ostatus').removeClass('xstatus');
};

var globalThermonuclearWar = function() {
	var gtw = {'xWins': 0, 'oWins': 0, 'xStarts': 0, 'oStarts': 0};
	var interval = window.setInterval(function() {
		if (xsTurn) {
			makeAIMove();
			if (!startNextTurn()) gtw['xWins']++;
			return;
		}
		if (osTurn) {
			makeAIMove();
			if (!startNextTurn()) gtw['oWins']++;
			return;
		}
		startGame(boardSize, aiPlayer);
		if (xsTurn) gtw['xStarts']++;
		if (osTurn) gtw['oStarts']++;
	}, 10);
	var cancel = function() {
		window.clearInterval(interval);
		return gtw;
	};
	gtw['interval'] = interval;
	gtw['cancel'] = cancel;
	return gtw;
};

var boardResized = function() {
	var oc = $('.board-outer-container');
	var ic = $('.board-inner-container');
	var s = Math.min(oc.width(), oc.height());
	var m = (-s/2) + 'px';
	ic.css('top', '50%').css('left', '50%');
	ic.css('margin-top', m).css('margin-left', m);
	ic.width(s).height(s);
};

$(document).ready(function() {
	var b = window.localStorage && window.localStorage['com.kreative.bridget.boardSize'] || 4;
	var p = window.localStorage && window.localStorage['com.kreative.bridget.numPlayers'] || 1;
	$('.start-game[data-boardsize]').removeClass('active');
	$('.start-game[data-boardsize='+b+']').addClass('active');
	$('.start-game[data-players]').removeClass('active');
	$('.start-game[data-players='+p+']').addClass('active');
	startGame(b, !(p-1));

	$('.start-game').each(function() {
		$(this).click(function() {
			var e = $(this);
			var b = e.attr('data-boardsize') || boardSize || 4;
			var p = e.attr('data-players') || (aiPlayer ? 1 : 2);
			$('.start-game[data-boardsize]').removeClass('active');
			$('.start-game[data-boardsize='+b+']').addClass('active');
			$('.start-game[data-players]').removeClass('active');
			$('.start-game[data-players='+p+']').addClass('active');
			startGame(b, !(p-1));
			if (window.localStorage) {
				window.localStorage['com.kreative.bridget.boardSize'] = b;
				window.localStorage['com.kreative.bridget.numPlayers'] = p;
			}
		});
	});

	$('.show-help').click(function() {
		$('.smokescreen').removeClass('hidden');
		$('.dialog').removeClass('hidden');
	});
	$('.hide-help').click(function() {
		$('.dialog').addClass('hidden');
		$('.smokescreen').addClass('hidden');
	});

	boardResized();
	$(window).resize(boardResized);

	window.globalThermonuclearWar = globalThermonuclearWar;
});

})(jQuery,window,document);