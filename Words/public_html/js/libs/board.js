

var Board = function(params) {
    this.context = params.context;
    this.width = params.width;
    this.height = params.height;
    this.rowCount = params.rowCount;
    this.columnCount = params.rowCount;
    this.cellLength = (params.width - 2 * params.delta) / params.rowCount;
    this.delta = params.delta;
    this.ratio = this.cellLength / 70;
    this.symbols = getAvailableSymbols(this.ratio);
    this.symbolIndexes = getSymbols();
    this.gameFinished = params.gameFinished;
    
    this.theme = [];
    this.theme.symbolColor = params.symbolColor || "#FFFFFF";
    this.theme.cellSpacer = params.cellSpacer || 1;
    this.theme.cellColor = params.cellColor || "#29ABE2";
    this.theme.cellColor2 = params.cellColor2 || "#29ABE2";
    this.theme.wordSearchColor = params.wordSearchColor || "#B8D6FB";
    this.theme.wordHoverColor = params.wordHoverColor || "#67E122";
    this.theme.wordFoundColor = params.wordFoundColor || "#008F8F";
    this.theme.wordWrongColor = params.wordWrongColor || "#FF4E4E";
    this.theme.spacerColor = params.spacerColor || "#FFFFFF";

    this.items = [];
    this.words = [];
    this.StartCell = null;
    this.EndCell = null;
    this.wordsFound = [];
    this.wordCells = [];
    this.timer = null;
    this.startTime = null;
    this.endTime = null;
    this.isGameFinished = false;
};

Board.prototype.FillItems = function(board, words) {
    this.items = board;
    this.words = words;

    for (index = 0; index < this.words.length; index++) {
        this.wordsFound[index] = false;
    }
    
    this.startTime = new Date();
    self = this;
    self.timer = setInterval(function() {self.ShowTime();}, 500);
};

Board.prototype.GetTimeElapsed = function() {
    if (this.startTime === undefined || this.startTime === null)
        return;
    currentTime = new Date();
    if (this.isGameFinished && this.endTime !== null && this.endTime !== undefined)
        currentTime = this.endTime;
    
    diff = Math.trunc((currentTime.getTime() - this.startTime.getTime()) / 1000);
    minutes = Math.trunc(diff / 60);
    seconds = diff % 60;
    if (seconds < 10)
        seconds = "0" + seconds;
    return minutes + ":" + seconds;
};

Board.prototype.ShowTime = function(color) {
    // draw table
    if (color !== null && color !== undefined)
        this.context.fillStyle = color;
    else 
        this.context.fillStyle = "silver";
    this.context.fillRect(this.width + this.delta, 0, 100, this.Scale(60));

    cellTop = this.Scale(40);
    cellLeft = this.width + this.delta + 20;
    
    timeElapsed = this.GetTimeElapsed();
    this.context.fillStyle = "black";
    this.context.font = this.Scale(32) + "px bpg_mrgvlovani_caps_2010Rg";
    this.context.fillText(timeElapsed, cellLeft, cellTop);
};

Board.prototype.DrawWords = function() {
    // draw table
    this.context.fillStyle = "silver";
    this.context.fillRect(this.width + this.delta, 0, 180, this.height);

    cellTop = this.Scale(100);
    cellLeft = this.width + this.delta + this.delta;
    
    for (index = 0; index < this.words.length; index++) {
        if (this.wordsFound[index])
            this.context.fillStyle = "black";
        else 
            this.context.fillStyle = "maroon";
        
        this.context.font = this.Scale(21) + "px bpg_mrgvlovani_caps_2010Rg";
        this.context.fillText(this.words[index], cellLeft, cellTop);
        
        if (this.wordsFound[index]) {
            this.context.beginPath();
            this.context.strokeStyle = this.context.fillStyle;
            this.context.lineWidth = 2;
            this.context.moveTo(cellLeft, cellTop - 8);
            wordWidth = this.context.measureText(this.words[index]).width;
            this.context.lineTo(cellLeft + wordWidth, cellTop - 8);
            this.context.stroke();            
        }
        
        cellTop += this.Scale(40) + this.delta;
    }
};

Board.prototype.Scale = function(size) {
    return Math.trunc(size * this.ratio);
};

Board.prototype.Draw = function() {
    // draw border
    this.context.fillStyle = this.theme.spacerColor;
    this.context.fillRect(0, 0, this.width, this.height);
    
    // draw cells
    for (var row = 0; row < this.rowCount; row ++) {
        for (var column = 0; column < this.columnCount; column++) {
            this.DrawCell(row, column);
        }
    }

    // draw detected words
    for (index = 0; index < this.wordCells.length; index++) {
        this.DrawWordLine(this.wordCells[index], this.theme.wordFoundColor);
    }
    
    // draw current word
    if (this.StartCell !== undefined && this.EndCell !== undefined && this.StartCell !== null && this.EndCell !== null) {
        cells = this.DetectWord();
        if (cells !== undefined && cells !== null && cells.length > 0) {
            this.DrawWordLine(new WordCell(this.StartCell, this.EndCell), this.theme.wordHoverColor);
        } else {
            if (this.StartCell.row === this.EndCell.row || this.StartCell.column === this.EndCell.column
                    || Math.abs(this.StartCell.row - this.EndCell.row) === Math.abs(this.StartCell.column - this.EndCell.column))
                this.DrawWordLine(new WordCell(this.StartCell, this.EndCell), this.theme.wordSearchColor);
            else
                this.DrawWordLine(new WordCell(this.StartCell, this.EndCell), this.theme.wordWrongColor);
        }
    }
    
    // fill board with symbols
    for (var row = 0; row < this.rowCount; row ++) {
        for (var column = 0; column < this.columnCount; column++) {
            try {
                currentSymbol = this.items[row].item[column];
                this.WriteCellText(row, column);
            } catch(x) {
                //console.info(row + " - " + column + " -- " + board.items[row].item[column]);
            }
        }
    }
    
    // draw words
    this.DrawWords();
};

Board.prototype.DrawWordLine = function(wordCell, color) {
    height = 0;
    width = 0;
    angle = 0;
    rectTop = 0;
    rectLeft = 0;
    
    if (wordCell.StartCell.column === wordCell.EndCell.column) {
        // vertical line
        height = (Math.abs(wordCell.EndCell.row - wordCell.StartCell.row) + 1) * this.cellLength;
        width = this.cellLength;
        angle = 0;
        rectLeft = wordCell.StartCell.column * this.cellLength;
        if (wordCell.StartCell.row < wordCell.EndCell.row) {
            rectTop = wordCell.StartCell.row * this.cellLength;
        } else {
            rectTop = wordCell.EndCell.row * this.cellLength;
        }
    } else if (wordCell.StartCell.row === wordCell.EndCell.row) {
        // horizontal line
        width = (Math.abs(wordCell.StartCell.column - wordCell.EndCell.column) + 1) * this.cellLength;
        height = this.cellLength;
        angle = 0;
        rectTop = wordCell.StartCell.row * this.cellLength;
        if (wordCell.StartCell.column < wordCell.EndCell.column) {
            rectLeft = wordCell.StartCell.column * this.cellLength;
        } else {
            rectLeft = wordCell.EndCell.column * this.cellLength;
        }
    } else {
        // diagonal line
        firstCell = wordCell.StartCell;
        secondCell = wordCell.EndCell;
        if (wordCell.StartCell.row > wordCell.EndCell.row) {
            secondCell = wordCell.StartCell;
            firstCell = wordCell.EndCell;
        }

        edgeA = (Math.abs(firstCell.row - secondCell.row) + 1) * this.cellLength;
        edgeB = (Math.abs(firstCell.column - secondCell.column) + 1) * this.cellLength;
        //width = (secondCell.row - firstCell.row + 1) * this.cellLength * 1.4142;
        width = Math.sqrt(edgeA*edgeA + edgeB*edgeB);
        height = this.cellLength;

        
        rectTop = firstCell.row * this.cellLength +  (secondCell.row - firstCell.row) * this.cellLength / 2;
        rectLeft = Math.min(firstCell.column, secondCell.column) * this.cellLength +
                (Math.abs(secondCell.column - firstCell.column) + 1) * this.cellLength / 2
                - width / 2;
        /*
        if (firstCell.column < secondCell.column) {
            angle = 45;
        } else {
            angle = 135;
        }
        */
        angle = Math.acos(edgeB / Math.sqrt(edgeA*edgeA + edgeB*edgeB)) * (180 / Math.PI);;
        if (firstCell.column >= secondCell.column) {
            angle = 360 - angle;
        }
        
    }
    
    cellTop = this.delta + rectTop;
    cellLeft = this.delta + rectLeft;


    // first save the untranslated/unrotated context
    this.context.save();

    this.context.beginPath();
    // move the rotation point to the center of the rect
    this.context.translate(cellLeft + width / 2, cellTop + height / 2);
    // rotate the rect
    this.context.rotate(angle * Math.PI / 180);

    // draw the rect on the transformed context
    // Note: after transforming [0,0] is visually [x,y]
    //       so the rect needs to be offset accordingly when drawn
    roundRect(this.context, -width / 2, -height / 2, width, height, 30, true);

    //this.context.stroke();
    this.context.fillStyle = color;
    this.context.fill();

    // restore the context to its untranslated/unrotated state
    this.context.restore();
};

Board.prototype.DrawCell = function(row, column, color) {
    cellTop = this.delta + row * this.cellLength;
    cellLeft = this.delta + column * this.cellLength;
    
    cellColor = (row + column) % 2 === 1? this.theme.cellColor : this.theme.cellColor2;
    if (color !== undefined && color !== null)
        cellColor = color;
    this.context.fillStyle = cellColor;
    this.context.fillRect(
            cellLeft + this.theme.cellSpacer, 
            cellTop + this.theme.cellSpacer, 
            this.cellLength - 2 * this.theme.cellSpacer, 
            this.cellLength - 2 * this.theme.cellSpacer);
};

Board.prototype.WriteCellText = function(row, column) {
    cellTop = this.delta + row * this.cellLength;
    cellLeft = this.delta + column * this.cellLength;
    
    this.context.fillStyle = this.theme.symbolColor;
    this.context.font = this.Scale(41) + "px bpg_mrgvlovani_caps_2010Rg";
    currentSymbol = this.GetSymbol(this.items[row].item[column]);
    this.context.fillText(currentSymbol.text, cellLeft + this.Scale(currentSymbol.deltaLeft), cellTop + this.Scale(currentSymbol.deltaTop));
};

Board.prototype.GetSymbol = function(character) {
    index = this.symbolIndexes.indexOf(character);
    if (index > -1)
        return this.symbols[index];
    else 
        return new Symbol(character, 80 * this.ratio, 30 * this.ratio);
};

Board.prototype.DetectWord = function() {
    startCell = this.StartCell;
    endCell = this.EndCell;
    
    // validate cells
    if (startCell === null || endCell === null) 
        return;
    if (startCell.column >= this.columnCount || startCell.row >= this.rowCount) {
        return;
    }
    if (endCell.column >= this.columnCount || endCell.row >= this.rowCount) {
        return;
    }
    
    // check lines
    if (endCell.column !== startCell.column && endCell.row !== startCell.row
            && Math.abs(endCell.column - startCell.column) !== Math.abs(endCell.row - startCell.row)) {
        return;
    }
    
    cells = [];
    index = 0;
    cells[index] = startCell;
    
    rowDelta = 0;
    rowDeltaSign = Math.sign(endCell.row - startCell.row);
    columnDelta = 0;
    columnDeltaSign = Math.sign(endCell.column - startCell.column);
    while ((rowDelta !== endCell.row - startCell.row) || (columnDelta !== endCell.column - startCell.column)) {
        index++;
        rowDelta += rowDeltaSign;
        columnDelta += columnDeltaSign;

        row = startCell.row +  rowDelta;
        column = startCell.column + columnDelta;
        cells[index] = new Cell(row, column);
    }
    
    word = "";
    for (index = 0; index < cells.length; index++) {
        cell = cells[index];
        word += this.items[cell.row].item[cell.column];
    }
    
    for (index = 0; index < this.words.length; index++) {
        if (word === this.words[index]) {
            this.wordsFound[index] = true;
            return cells;
        }
    }
    
    return;
};

Board.prototype.StartFrom = function(rectLeft, rectTop) {
    if (this.isGameFinished) 
        return;
    
    cell = this.GetCellAt(rectLeft, rectTop);
  
    this.StartCell = cell;
    this.EndCell = null;
};

Board.prototype.MoveTo = function(rectLeft, rectTop) {
    if (this.StartCell === null)
        return;
    
    cell = this.GetCellAt(rectLeft, rectTop);
    if (cell === null && cell === undefined)
        return;
    
    if (cell !== null && cell !== undefined) {
        if (this.EndCell === null || this.EndCell === undefined 
                || this.EndCell.column !== cell.column || this.EndCell.row !== cell.row) {
            this.EndCell = cell;
            this.Draw();
        }
    }
};

Board.prototype.EndTo = function(rectLeft, rectTop) {
    cell = this.GetCellAt(rectLeft, rectTop);
    if (cell === null && cell === undefined)
        return;

    this.EndCell = cell;
    cells = this.DetectWord();

    if (cells !== null && cells !== undefined) {
        for (index = 0; index < cells.length; index++) {
            cell = cells[index];
            //console.info(this.items[cell.row].item[cell.column]);
            this.DrawCell(cell.row, cell.column, "green");
        }

        if (cells !== null && cells.length > 0) {
            this.wordCells[this.wordCells.length] = new WordCell(this.StartCell, this.EndCell);        
        }
        
        // check if all games were found
        allFound = true;
        for (index = 0; index < this.wordsFound.length; index++) {
            if (!this.wordsFound[index]) {
                allFound = false;
                break;
            }
        }
        if (allFound) {
            // game finished
            this.isGameFinished = true;
            this.endTime = new Date();
            if (this.timer !== null && this.timer !== undefined)
                clearInterval(this.timer);
            if (this.gameFinished !== null && this.gameFinished !== undefined)
                this.gameFinished(this.GetTimeElapsed());
        }
    }
    this.StartCell = null;
    this.EndCell = null;
    
    this.Draw();
    if (this.isGameFinished) {
        this.ShowTime("red");
    }
};

Board.prototype.GetCellAt = function(rectLeft, rectTop) {
    if (rectLeft > this.width || rectTop > this.height)
        return null;
    column = Math.trunc((rectLeft - this.delta) / this.cellLength);
    row = Math.trunc((rectTop - this.delta) / this.cellLength);
    return new Cell(row, column);
};

var WordCell = function(startCell, endCell) {
    this.StartCell = startCell;
    this.EndCell = endCell;
};

var Cell = function(row, column) {
    this.row = row;
    this.column = column;
};

var Symbol = function(text, deltaTop, deltaLeft) {
    this.text = text;
    this.deltaTop = deltaTop;
    this.deltaLeft = deltaLeft;
};

function getAvailableSymbols(ratio) {
    return [
        new Symbol("ა", 80 * ratio, 30 * ratio), 
        new Symbol("ბ", 80 * ratio, 30 * ratio),
        new Symbol("გ", 80 * ratio, 30 * ratio),
        new Symbol("დ", 80 * ratio, 20 * ratio),
        new Symbol("ე", 80 * ratio, 30 * ratio),
        new Symbol("ვ", 80 * ratio, 30 * ratio),
        new Symbol("ზ", 80 * ratio, 20 * ratio),
        new Symbol("თ", 80 * ratio, 20 * ratio),
        new Symbol("ი", 80 * ratio, 30 * ratio),
        new Symbol("კ", 80 * ratio, 30 * ratio),
        new Symbol("ლ", 80 * ratio, 10 * ratio),
        new Symbol("მ", 80 * ratio, 30 * ratio),
        new Symbol("ნ", 80 * ratio, 30 * ratio),
        new Symbol("ო", 80 * ratio, 20 * ratio),
        new Symbol("პ", 80 * ratio, 30 * ratio),
        new Symbol("ჟ", 80 * ratio, 30 * ratio),
        new Symbol("რ", 80 * ratio, 20 * ratio),
        new Symbol("ს", 80 * ratio, 30 * ratio),
        new Symbol("ტ", 80 * ratio, 20 * ratio),
        new Symbol("უ", 80 * ratio, 30 * ratio),
        new Symbol("ფ", 80 * ratio, 20 * ratio),
        new Symbol("ქ", 80 * ratio, 30 * ratio),
        new Symbol("ღ", 80 * ratio, 20 * ratio),
        new Symbol("ყ", 80 * ratio, 30 * ratio),
        new Symbol("შ", 80 * ratio, 30 * ratio),
        new Symbol("ჩ", 80 * ratio, 30 * ratio),
        new Symbol("ც", 80 * ratio, 30 * ratio),
        new Symbol("ძ", 80 * ratio, 30 * ratio),
        new Symbol("წ", 80 * ratio, 30 * ratio),
        new Symbol("ჭ", 80 * ratio, 30 * ratio),
        new Symbol("ხ", 80 * ratio, 30 * ratio),
        new Symbol("ჯ", 80 * ratio, 20 * ratio),
        new Symbol("ჰ", 80 * ratio, 30 * ratio)
    ];
}
/*
function getAvailableSymbols(ratio) {
    return [
        new Symbol("ა", 80 * ratio, 30 * ratio), 
        new Symbol("ბ", 85 * ratio, 25 * ratio),
        new Symbol("გ", 60 * ratio, 20 * ratio),
        new Symbol("დ", 60 * ratio, 10 * ratio),
        new Symbol("ე", 60 * ratio, 25 * ratio),
        new Symbol("ვ", 60 * ratio, 20 * ratio),
        new Symbol("ზ", 85 * ratio, 15 * ratio),
        new Symbol("თ", 80 * ratio, 10 * ratio),
        new Symbol("ი", 80 * ratio, 30 * ratio),
        new Symbol("კ", 60 * ratio, 25 * ratio),
        new Symbol("ლ", 60 * ratio, 2 * ratio),
        new Symbol("მ", 85 * ratio, 25 * ratio),
        new Symbol("ნ", 85 * ratio, 25 * ratio),
        new Symbol("ო", 80 * ratio, 10 * ratio),
        new Symbol("პ", 85 * ratio, 25 * ratio),
        new Symbol("ჟ", 60 * ratio, 25 * ratio),
        new Symbol("რ", 85 * ratio, 15 * ratio),
        new Symbol("ს", 85 * ratio, 25 * ratio),
        new Symbol("ტ", 65 * ratio, 15 * ratio),
        new Symbol("უ", 60 * ratio, 15 * ratio),
        new Symbol("ფ", 60 * ratio, 15 * ratio),
        new Symbol("ქ", 70 * ratio, 20 * ratio),
        new Symbol("ღ", 60 * ratio, 15 * ratio),
        new Symbol("ყ", 60 * ratio, 25 * ratio),
        new Symbol("შ", 85 * ratio, 20 * ratio),
        new Symbol("ჩ", 85 * ratio, 25 * ratio),
        new Symbol("ც", 60 * ratio, 20 * ratio),
        new Symbol("ძ", 85 * ratio, 20 * ratio),
        new Symbol("წ", 70 * ratio, 20 * ratio),
        new Symbol("ჭ", 70 * ratio, 20 * ratio),
        new Symbol("ხ", 85 * ratio, 20 * ratio),
        new Symbol("ჯ", 60 * ratio, 10 * ratio),
        new Symbol("ჰ", 85 * ratio, 20 * ratio)
    ];
}
*/

function getSymbols() {
    return "აბგდევზთიკლმნოპჟრსტუფქღყშჩცძწჭხჯჰ";
}