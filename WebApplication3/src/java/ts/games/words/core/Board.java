/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ts.games.words.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Lasha
 */
public class Board {

    private final String SYMBOLS = "ზხცვბნმასდფგჰჯკლქწერტყუიოპძჩშჟჭღთ";
    private int length;
    private Character[][] items;
    private List<String> words;
    private int gameId;

    public Board() {
    }
    
    public Board(int length, List<String> words, int gameId) {
        this.items = null;
        this.length = length;
        this.words = words;
        this.gameId = gameId;
        fillBoard();
    }

    private void fillBoard() {
        Random random = new Random();
        searchWordPlace(new Character[length][length], 0, random);
        fillEmptyPlaces(random);
    }
    
    private void fillEmptyPlaces(Random random) {
        StringBuilder builder = new StringBuilder(SYMBOLS);
        words.stream().forEach((word) -> {
            builder.append(word);
        });
        char[] symbols = builder.toString().toCharArray();
        for (int x = 0; x < length; x++) {
            for (int y = 0; y < length; y++) {
                if (items[x][y] == null) {
                    int index = random.nextInt(symbols.length);
                    items[x][y] = symbols[index];
                }
            }
        }
    }

    private void searchWordPlace(Character[][] currentBoard, int wordIndex, Random random) {
        if (wordIndex >= words.size()) {
            items = currentBoard;
            return;
        }

        String word = words.get(wordIndex);

        List<Combination> combinations = generateRandomCombinations(random);
        for (Combination direction : combinations) {
            
            boolean[][] positionsUsed = generatePositionBoard(currentBoard);
            
            int deltaX = random.nextInt(length);
            int deltaY = random.nextInt(length);
            for (int x = 0; x < length; x++) {
                for (int y = 0; y < length; y++) {

                    int currentX = (x + deltaX) % length;
                    int currentY = (y + deltaY) % length;

                    if (positionsUsed[currentX][currentY])
                        continue;
                    
                    if (canPlaceWord(currentBoard, currentX, currentY, word, direction)) {
                        Character[][] newBoard = currentBoard.clone();
                        placeWord(newBoard, currentX, currentY, word, direction);
                        searchWordPlace(newBoard, wordIndex + 1, random);
                        
                        // check result
                        if (items != null) {
                            return;
                        }
                        
                    }
                }
            }
        }
    }

    private void placeWord(Character[][] board, int x, int y, String word, Combination direction) {
        for (char wordChar : word.toCharArray()) {
            board[x][y] = wordChar;
            x += direction.getDeltaX();
            y += direction.getDeltaY();
        }
    }
    
    private boolean canPlaceWord(Character[][] board, int x, int y, String word, Combination direction) {
        for (char wordChar : word.toCharArray()) {
            
            if (x < 0 || x >= length || y < 0 || y >= length)
                return false;
            
            if (board[x][y] != null && board[x][y] != wordChar)
                return false;
            
            x += direction.getDeltaX();
            y += direction.getDeltaY();
        }
        return true;
    }
    
    private boolean[][] generatePositionBoard(Character[][] currentBoard) {
        boolean[][] positionsUsed = new boolean[length][length];
        for (int x = 0; x < length; x++) {
            for (int y = 0; y < length; y++) {
                positionsUsed[x][y] = currentBoard[x][y] != null;
            }
        }
        return positionsUsed;
    }

    private List<Combination> generateRandomCombinations(Random random) {
        List<Combination> list = new ArrayList<>();
        for (int deltaX = -1; deltaX < 2; deltaX++) {
            for (int deltaY = -1; deltaY < 2; deltaY++) {
                if (deltaX == 0 && deltaY == 0) {
                    continue;
                }
                Combination combination = new Combination(deltaX, deltaY);
                int index = random.nextInt(list.size() + 1);
                list.add(index, combination);
            }
        }
        return list;
    }

    public Character[][] getItems() {
        return items;
    }

    public void setItems(Character[][] items) {
        this.items = items;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }
    
}

class Combination {

    private int deltaX;
    private int deltaY;

    public Combination(int deltaX, int deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    public int getDeltaX() {
        return deltaX;
    }

    public void setDeltaX(int deltaX) {
        this.deltaX = deltaX;
    }

    public int getDeltaY() {
        return deltaY;
    }

    public void setDeltaY(int deltaY) {
        this.deltaY = deltaY;
    }

    @Override
    public int hashCode() {
        return (2 + deltaX) * 10 + 2 + deltaY;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Combination other = (Combination) obj;
        if (this.deltaX != other.deltaX) {
            return false;
        }
        return this.deltaY == other.deltaY;
    }

}
