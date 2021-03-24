# Text-Based Chinese Checkers and AI

Chinese checkers game for two players (more players to be added later). Either player can be a human or an AI, depending on if the first two command-line arguments are `human` or `ai`. Each turn, the pieces that can move are shown by their position `(row_number, column_number)`. The player types in the piece they want to move with the form `row_number column_number` and the program will list all the valid moves that the player's chosen piece can move. The player then types in the move they want to take with the form `row_number column_number`.

# How the position system works

The board is stored internally as a 2D array, but because the two axes of a Chinese checkers board are 60° offset from each other and the board takes on a 6-sided star pattern, some of the locations in the array aren't used, but the positions are still consistent with the 2D array positions (i.e., the position (4, 0) is the fourth row, zeroth column where the zeroth column is the one farthest to the left and the zeroth row is the first row at the very top). The board is automatically displayed after every move.

# How the AI works

The AI uses alpha-beta pruning to find the best move, although to keep the run-time reasonable (about 5 seconds at the high end), the depth limit is set to 5. Because Chinese checkers requires dozens of moves to reach the end of a game, AI success is limited by this depth limit.
