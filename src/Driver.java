public class Driver {
    public static void main(String[] args) {
        short totalPlayers = 2;
        // args should be "human" or "ai"
        String player1type = args[0];
        String player2type = args[1];
        Game game = new Game(player1type, player2type, 5);  // depth 5 allows for AI to not take too long
        System.out.println(game.toStringOrthogonal());
        short playerNum = 1;
        short turn = 1;
        while (!game.isGameOver()) {
            System.out.println("Turn: " + turn);
            game.chooseMove(playerNum);
            playerNum++;
            if (playerNum > totalPlayers) {
                playerNum = 1;
                turn++;
            }
        }
        System.out.println("PLAYER " + game.getWinner() + "WON!");
    }
}
