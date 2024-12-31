public class Main {
    static Game game = new Game();
    public static void main(String[] args) {
        game.startGame();
    }
}

class Game {
    static UI ui = new UI();

    Game() {}
    
    void startGame() {
        ui.menu();
    }

    void twoPlayer() {
        ui.twoPlayer();
    }

    void resetGame() {
        ui.cleanUpGame();
    }
}