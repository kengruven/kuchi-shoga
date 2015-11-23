var SVG_NS = "http://www.w3.org/2000/svg";

function mksvg(name) {
    return $(document.createElementNS(SVG_NS, name));
}

function renderHit(x, y, strength) {
    return mksvg("circle").attr('cx', x).attr('cy', y).attr('r', strength);
}


var KA_LENGTH = 6;
// TODO: other size constants here!

function renderLine(parent, x, y, hits) {
    var l = mksvg("line");
    l.attr('x1', x-20).attr('y1', y);
    l.attr('x2', hits.length*20+10).attr('y2', y);
    parent.append(l);

    for (var i=0; i<hits.length; i++) {
        if (hits[i] === 'x') {
            mksvg("path").attr('d',
                               "M" + (x+20*i-KA_LENGTH) + "," + (y-KA_LENGTH) + " " +
                               "l" + 2*KA_LENGTH + "," + 2*KA_LENGTH + " " +
                               "m0," + -2*KA_LENGTH + " " +
                               "l" + -2*KA_LENGTH + "," + 2*KA_LENGTH)
                         .appendTo(parent);
        } else {
            var str = {'*':8, '.':3, ' ':0};
            parent.append(renderHit(x + 20*i, y, str[hits[i]]));
        }
    }
}

// ----------------------------------------

// given a string like '*.**', make an SVG block for just that.
function renderMeasure(container, measure) {
    var svgElement = mksvg("svg").attr('width', (measure.length+1)*20).attr('height', 25)
        .appendTo(container);
    var shiftElement = mksvg("g").attr('class', 'toplevel').appendTo(svgElement);
    renderLine(shiftElement, 15, 15, measure);
}

// render a list of {label, hits, repeat, notes} objects
function renderPiece(container, lines) {
    var table = $("<table/>").appendTo(container);

    for (var i=0; i<lines.length; i++) {
        var line = lines[i];
        var row = $("<tr/>").appendTo(table);
        var labelCell = $("<td/>").addClass('label').text(line.label).appendTo(row);

        var hitsCell = $("<td/>").appendTo(row);
        var measures = line.hits.split(" ");
        for (var j=0; j<measures.length; j++) {
            renderMeasure(hitsCell, measures[j]);
        }

        var repeatCell = $("<td/>").addClass('repeat').text(line.repeat ? "×" + line.repeat : "").appendTo(row);
        var notesCell = $("<td/>").addClass('notes').text(line.notes).appendTo(row);
    }
}
