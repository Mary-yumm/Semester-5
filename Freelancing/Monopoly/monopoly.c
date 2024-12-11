#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>

#define BOARD_SIZE 40
#define MAX_PLAYERS 4
#define START_BALANCE 1000

typedef struct
{
    char symbol;                // Player symbol (non-alphanumeric)
    int position;               // Current position on the board
    int balance;                // Player's balance
    int properties[BOARD_SIZE]; // 1 if owned, -1 if mortgaged, 0 otherwise
    int jailTurns;              // Number of double turns to go to jail
    int inJail;                 // Flag for jail status
    int rolls;                  // Total dice rolls
    int jailVisits;             // Number of times in jail
    int sumOfRolls;             // Total sum of dice rolls (used for average calculation)

} Player;

typedef struct
{
    char name[30];   // Name of property
    int price;       // Cost to buy
    int rent;        // Rent amount
    int mortgage;    // Mortgage value
    int owner;       // -1 if unowned, otherwise player index
    int isMortgaged; // 1 if mortgaged, 0 otherwise
} Property;

Property board[BOARD_SIZE];
int numPlayers;
Player players[MAX_PLAYERS];

// Function Prototypes
void initializeBoard();
void initializePlayers();
void rollDice(int *dice1, int *dice2,Player *player);
void unMortgageProperties(Player *player);
int mortgageProperty(Player *player, int requiredAmount);
void playerTurn(int playerIndex);
void printBoard();
int checkGameEnd();
void handleProperty(Player *player, int propertyIndex, int doubles);
void handleJailLogic(Player *player, int doubles);

// Game Initialization
void initializeBoard()
{
    // Special Tiles
    board[0] = (Property){"Go", 0, 0, 0, -1, 0};
    board[4] = (Property){"Income Tax", 0, -100, 0, -1, 0};
    board[10] = (Property){"Go to Jail", 0, 0, 0, -1, 0};
    board[20] = (Property){"Free Parking", 0, 0, 0, -1, 0};
    board[30] = (Property){"Go to Jail", 0, 0, 0, -1, 0};
    board[38] = (Property){"Luxury Tax", 0, -100, 0, -1, 0};

    // Properties
    board[1] = (Property){"Mediterr", 60, 2, 30, -1, 0};
    board[3] = (Property){"Baltic", 60, 4, 30, -1, 0};
    board[6] = (Property){"Oriental", 100, 6, 50, -1, 0};
    board[8] = (Property){"Vermont", 100, 6, 50, -1, 0};
    board[9] = (Property){"Connecticut", 120, 8, 60, -1, 0};
    board[11] = (Property){"St. Charles", 140, 10, 70, -1, 0};
    board[13] = (Property){"States", 140, 10, 70, -1, 0};
    board[14] = (Property){"Virginia", 160, 12, 80, -1, 0};
    board[16] = (Property){"St. James", 180, 14, 90, -1, 0};
    board[18] = (Property){"Tennessee", 180, 14, 90, -1, 0};
    board[19] = (Property){"New York", 200, 16, 100, -1, 0};
    board[21] = (Property){"Kentucky", 220, 18, 110, -1, 0};
    board[23] = (Property){"Indiana", 220, 18, 110, -1, 0};
    board[24] = (Property){"Illinois", 240, 20, 120, -1, 0};
    board[26] = (Property){"Atlantic", 260, 22, 130, -1, 0};
    board[27] = (Property){"Ventnor", 260, 22, 130, -1, 0};
    board[29] = (Property){"Marvin", 280, 24, 140, -1, 0};
    board[31] = (Property){"Pacific", 300, 26, 150, -1, 0};
    board[32] = (Property){"N Carolina", 300, 26, 150, -1, 0};
    board[34] = (Property){"Pennsylvania", 320, 28, 160, -1, 0};
    board[37] = (Property){"Park Place", 350, 35, 175, -1, 0};
    board[39] = (Property){"Boardwalk", 400, 50, 200, -1, 0};

    // Railroads
    board[5] = (Property){"Reading Rail", 200, 25, 100, -1, 0};
    board[15] = (Property){"P Rail", 200, 25, 100, -1, 0};
    board[25] = (Property){"B. & O. Rail", 200, 25, 100, -1, 0};
    board[35] = (Property){"Sh Line Rail", 200, 25, 100, -1, 0};

    // Utilities
    board[12] = (Property){"Electric Company", 150, 10, 75, -1, 0};
    board[28] = (Property){"Water Works", 150, 10, 75, -1, 0};

    board[2] = (Property){"Empty Tile", 0, 0, 0, -1, 0};
    board[7] = (Property){"Empty Tile", 0, 0, 0, -1, 0};
    board[22] = (Property){"Empty Tile", 0, 0, 0, -1, 0};
    board[33] = (Property){"Empty Tile", 0, 0, 0, -1, 0};
    board[17] = (Property){"Empty Tile", 0, 0, 0, -1, 0};
    board[36] = (Property){"Empty Tile", 0, 0, 0, -1, 0};
}

void initializePlayers()
{
    do
    {
        printf("Enter number of players (2-4): ");
        scanf("%d", &numPlayers);

        // Validate the input
        if (numPlayers < 2 || numPlayers > 4)
        {
            printf("Invalid input! Please enter a number between 2 and 4.\n");
        }
    } while (numPlayers < 2 || numPlayers > 4); // Repeat until valid input is provided

    for (int i = 0; i < numPlayers; i++)
    {
        players[i].symbol = '#' + i;
        players[i].position = 0;
        players[i].balance = START_BALANCE;
        players[i].jailTurns = 0;
        players[i].inJail = 0;
        players[i].rolls = 0;
        players[i].jailVisits = 0;
        for (int j = 0; j < BOARD_SIZE; j++)
        {
            players[i].properties[j] = 0;
        }
    }
}

void rollDice(int *dice1, int *dice2, Player *player)
{
    *dice1 = rand() % 6 + 1;
    *dice2 = rand() % 6 + 1;
    player->sumOfRolls += *dice1 + *dice2; // Add the sum of the dice roll to the player's total sum
    player->rolls++;  // Increase the total number of rolls
}

void unMortgageProperties(Player *player)
{
    if (player != NULL)
    {
        for (int i = 0; i < BOARD_SIZE; i++)
        {
            Property *property = &board[i];
            if (property->owner == (player - players) && property->isMortgaged)
            {
                // Check if player has enough money to un-mortgage
                if (player->balance > property->mortgage)
                {
                    printf("Player %c has enough money to un-mortgage %s.\n", player->symbol, property->name);
                    printf("Un-mortgaging %s for $%d.\n", property->name, property->mortgage);
                    player->balance -= property->mortgage; // Deduct the mortgage cost
                    property->isMortgaged = 0;             // Mark as no longer mortgaged
                    printf("Player %c now owns %s (un-mortgaged).\n", player->symbol, property->name);
                }
            }
        }
    }
    else
    {
        printf("player null in unmortgage\n");
    }
}

// Function to mortgage properties when the player doesn't have enough money
int mortgageProperty(Player *player, int requiredAmount)
{
    if (player != NULL)
    {
        int totalMortgageValue = 0;
        int mortgagedCount = 0;

        for (int i = 0; i < BOARD_SIZE; i++)
        {
            Property *property = &board[i];
            // Check if the property is owned by the player and not already mortgaged
            if (property->owner == (player - players) && !property->isMortgaged)
            {
                // Mortgage the property
                printf("Player %c is mortgaging %s for $%d.\n", player->symbol, property->name, property->mortgage);
                player->balance += property->mortgage; // Add mortgage value to player's balance
                property->isMortgaged = 1;             // Mark property as mortgaged
                property->owner = -1;                  // The property is no longer owned by the player
                player->properties[i] = -1;            // Update the properties array to reflect mortgaging
                totalMortgageValue += property->mortgage;
                mortgagedCount++;

                printf("Player %c now has $%d.\n", player->symbol, player->balance);

                // If the player has enough money after mortgaging, break out of the loop
                if (player->balance >= requiredAmount)
                {
                    break;
                }
            }
        }

        // If the mortgaged properties' total value is still not enough to cover the rent or cost
        if (player->balance < requiredAmount)
        {
            printf("Player %c still doesn't have enough money after mortgaging %d properties. Total mortgaged: $%d\n", player->symbol, mortgagedCount, totalMortgageValue);
            return 0; // Player could not mortgage enough properties
        }

        printf("Player %c has mortgaged %d properties to raise $%d to cover the cost.\n", player->symbol, mortgagedCount, totalMortgageValue);
        return 1; // Return true indicating successful mortgaging
    }
    else
    {
        printf("Invalid player.\n");
        return 0;
    }
}

void playerTurn(int playerIndex)
{
    Player *player = &players[playerIndex];

    int dice1, dice2;
    rollDice(&dice1, &dice2,player);
    int doubles = 0;
    if (dice1 == dice2)
    {
        // jail condition satisfied
        doubles = 1;
    }

    printf("Player %c rolled %d and %d\n", player->symbol, dice1, dice2);

    if (player->inJail == 0)
    {
        int propindex = (player->position + dice1 + dice2) % BOARD_SIZE;
        // Handle un-mortgaging properties before handling property logic
        if (player->balance > 500)
        {
            printf("Player %c has a balance of $%d, checking for mortgaged properties...\n", player->symbol, player->balance);
            unMortgageProperties(player);
        }
        handleProperty(player, propindex, doubles);
    }
    else
    {
        // Handle jail logic in a separate function
        handleJailLogic(player, doubles);

        // Reset jailTurns if not in jail and no doubles are rolled
        if (doubles == 0)
        {
            player->jailTurns = 0;
        }
    }
}

// Function to handle property logic (buying properties, paying rent, etc.)
void handleProperty(Player *player, int propertyIndex, int doubles)
{
    Property *property = &board[propertyIndex];
    if (strcmp(property->name, "Go to Jail") == 0)
    {
        player->inJail = 1;
        player->jailTurns = 0;
        player->jailVisits++;
        printf("Player %c is sent to jail\n", player->symbol);
        player->position = propertyIndex;

        return;
    }
    else if (strcmp(property->name, "Income Tax") == 0)
    {
        player->balance -= 100; // Pay $100 to the bank
        printf("Player %c pays $100 to Income Tax\n", player->symbol);
        player->position = propertyIndex;

        return;
    }
    else if (strcmp(property->name, "Luxury Tax") == 0)
    {
        player->balance -= 100; // Pay $100 to the bank
        printf("Player %c pays $100 Luxury Tax\n", player->symbol);
        player->position = propertyIndex;

        return;
    }
    else if (strcmp(property->name, "Free Parking") == 0)
    {
        printf("Player %c landed on Free Parking. Nothing happens.\n", player->symbol);
        player->position = propertyIndex;

        return;
    }
    else if (strcmp(property->name, "Go") == 0)
    {
        // Nothing happens when landing on "Go", just print and return
        printf("Player %c landed on Go. They pass Go and collect $200.\n", player->symbol);
        player->balance += 200; // Player receives $200
        player->position = propertyIndex;

        return;
    }
    else if (strcmp(property->name, "Empty Tile") == 0)
    {
        printf("Player %c lies on an Empty Tile. Nothing happens.\n", player->symbol);
        player->position = propertyIndex;

        return;
    }

    // Handle jail logic in a separate function
    handleJailLogic(player, doubles);

    // Reset jailTurns if not in jail and no doubles are rolled
    if (doubles == 0)
    {
        player->jailTurns = 0;
    }

    // If the property is not owned, attempt to buy it
    if (property->owner == -1)
    {
        int bal = player->balance;
        int prop = property->price;
        int rent = property->rent;
        int cost = property->price;

        if (bal > prop)
        {
            if (bal >= 500)
            {
                printf("Player %c buys %s for $%d\n", player->symbol, property->name, property->price);
                player->balance -= property->price;
                property->owner = player - players;    // Assigning ownership
                player->properties[propertyIndex] = 1; // Mark the property as owned by this player
                player->position = propertyIndex;
                property->isMortgaged = 0; // Ensure the property is no longer mortgaged

                return;
            }
            else
            {
                printf("Player %c does not have enough money to buy.\n", player->symbol);
                int isMortgaged = mortgageProperty(player, cost);
                if (isMortgaged == 1)
                {
                    printf("Player %c buys %s for $%d\n", player->symbol, property->name, property->price);
                    player->balance -= property->price;
                    property->owner = player - players;    // Assigning ownership
                    player->properties[propertyIndex] = 1; // Mark the property as owned by this player
                    player->position = propertyIndex;
                    property->isMortgaged = 0; // Ensure the property is no longer mortgaged
                }
                return;
            }
        }
        else
        {
            printf("Player %c does not have enough money to buy...\n", player->symbol);
            int isMortgaged = mortgageProperty(player, cost);
            if (isMortgaged == 1)
            {
                printf("Player %c buys %s for $%d\n", player->symbol, property->name, property->price);
                player->balance -= property->price;
                property->owner = player - players;    // Assigning ownership
                player->properties[propertyIndex] = 1; // Mark the property as owned by this player
                player->position = propertyIndex;
                property->isMortgaged = 0; // Ensure the property is no longer mortgaged
                return;
            }
        }
        if (bal < rent)
        {
            printf("Player %c cannot afford to pay rent of $%d.\n", player->symbol, property->rent);
            printf("Attempting to mortgage a property...\n");
            int isMortgaged = mortgageProperty(player, cost);
            if (isMortgaged == 1)
            {
                printf("Player %c buys %s for $%d\n", player->symbol, property->name, property->price);
                player->balance -= property->price;
                property->owner = player - players;    // Assigning ownership
                player->properties[propertyIndex] = 1; // Mark the property as owned by this player
                player->position = propertyIndex;
                property->isMortgaged = 0; // Ensure the property is no longer mortgaged
            }
        }
        else
        {
            printf("Player %c does not have enough money to buy and no other player has yet owned it ...\n", player->symbol);
            printf("Attempting to mortgage a property...\n");
            int isMortgaged = mortgageProperty(player, cost);
            if (isMortgaged == 1)
            {
                printf("Player %c buys %s for $%d\n", player->symbol, property->name, property->price);
                player->balance -= property->price;
                property->owner = player - players;    // Assigning ownership
                player->properties[propertyIndex] = 1; // Mark the property as owned by this player
                player->position = propertyIndex;
                property->isMortgaged = 0; // Ensure the property is no longer mortgaged
            }
        }
    }
    else if (property->owner != (player - players) && property->owner != -1)
    {
        // Property is owned by someone else, calculate and pay rent
        Player *owner = &players[property->owner];
        int rent = property->isMortgaged ? 0 : property->rent; // No rent if mortgaged
        if (player->balance >= rent)
        {
            printf("Player %c pays rent $%d to Player %c\n", player->symbol, rent, owner->symbol);
            player->balance -= rent;
            owner->balance += rent;
            player->position = propertyIndex;
        }
        else
        {
            printf("Player %c cannot afford to pay rent.\n");
        }
    }
}

// Function to handle jail logic (landing on jail or rolling doubles)
void handleJailLogic(Player *player, int doubles)
{
    if (player == NULL)
    {
        printf("Error: Player is NULL in handleJailLogic.\n");
        return;
    }
    if (player->inJail == 1) // If the player is already in jail
    {
        if (doubles == 1) // Player rolled doubles, get out of jail
        {
            player->inJail = 0;
            player->jailTurns = 0;
            printf("Player %c is out of the jail\n", player->symbol);
        }
        else
        {
            printf("Player %c is still in jail\n", player->symbol);
        }
        return;
    }
    else if (doubles == 1) // If player rolled doubles while not in jail
    {
        printf("Player %c rolled doubles\n", player->symbol);
        player->jailTurns++;
    }
    if (player->jailTurns == 3) // If player has rolled three doubles in a row
    {
        player->inJail = 1;
        player->jailTurns = 0;
        player->jailVisits++;
        printf("Player %c is sent to jail for rolling three doubles in a row\n", player->symbol);
    }
    else
    {
        printf("Player %c is not in jail\n", player->symbol);
    }
}

// Function to check if the game has ended
int checkGameEnd()
{
    int mortgagedAllOwnedPropertiesCount = 0;

    for (int i = 0; i < numPlayers; i++)
    {
        // Check if the player has mortgaged all owned properties
        int mortgagedCount = 0;
        for (int j = 0; j < BOARD_SIZE; j++)
        {
            if (players[i].balance < 0)
            {
                return 1;
            }
            // Count mortgaged owned properties
            if (players[i].properties[j] == -1) // Property mortgaged
            {
                mortgagedCount++;
            }
        }

        // Check if the player has mortgaged all of their owned properties
        int totalOwnedProperties = 0;
        for (int j = 0; j < BOARD_SIZE; j++)
        {
            if ((players[i].properties[j] == 1) || (players[i].properties[j] == -1)) // Property owned
            {
                totalOwnedProperties++;
            }
        }

        if (mortgagedCount == totalOwnedProperties && totalOwnedProperties > 0) // If mortgaged all owned properties
        {
            mortgagedAllOwnedPropertiesCount++;
        }
    }

    // If at least one player has mortgaged all their owned properties, game ends
    if (mortgagedAllOwnedPropertiesCount > 0)
    {
        return 1; // Game has ended
    }

    return 0; // Game is still ongoing
}

// Print Board
void printBoard()
{

    for (int i = 0; i < 20 * 11; i++)
    {
        printf("=");
    }
    printf("\n\n");
    for (int i = 0; i < BOARD_SIZE; i++)
    {
        if (i <= 10)
        { // First row
            printf("| %-12s ", board[i].name);

            // Add player symbols if any players are at this position
            int player_count = 0;
            for (int j = 0; j < numPlayers; j++)
            {
                if (players[j].position == i)
                {
                    printf("%c", players[j].symbol);
                    player_count++;
                }
            }
            // Adjust spacing based on the number of players
            for (int k = 0; k < (4 - player_count); k++)
            {
                printf(" "); // Add spaces to align
            }

            if (i % 10 == 0 && i != 0)
            {
                printf("\n\n");
            }
        }
        else if (i > 10 && i < 29)
        { // Middle rows
          // Print the first box name
            printf("| %-12s ", board[i].name);
            int player_count = 0;

            for (int j = 0; j < numPlayers; j++)
            {
                if (players[j].position == i)
                {
                    printf("%c", players[j].symbol);
                    player_count++;
                }
            }
            // Adjust spacing based on the number of players
            for (int k = 0; k < (4 - player_count); k++)
            {
                printf(" "); // Add spaces
            }
            printf("|");

            // Calculate the spaces in between
            int total_row_width = 19 * 11;                    // Assuming 10 boxes in a row
            int remaining_width = total_row_width - (2 * 19); // Width of spaces between the first and last boxes

            int num_spaces = remaining_width / 19; // Convert width into space units (17 characters per box)

            for (int j = 0; j < num_spaces - 1; j++)
            {
                printf("                   "); // Print blank spaces for the middle
            }
            printf("                  ");

            i++; // Move to the last box of the row
            if (i >= BOARD_SIZE)
            {
                break;
            }

            // Print the last box name
            printf("| %-12s ", board[i].name);
            player_count = 0;

            for (int j = 0; j < numPlayers; j++)
            {
                if (players[j].position == i)
                {
                    printf("%c", players[j].symbol);
                    player_count++;
                }
            }
            // Adjust spacing based on the number of players
            for (int k = 0; k < (4 - player_count); k++)
            {
                printf(" "); // Add spaces
            }

            printf("\n\n");
        }
        else if (i >= 29 && i <= 39)
        { // Last row
            // Add player symbols if any players are at this position
            printf("| %-12s ", board[i].name);
            int player_count = 0;
            for (int j = 0; j < numPlayers; j++)
            {
                if (players[j].position == i)
                {
                    printf("%c", players[j].symbol);
                    player_count++;
                }
            }
            // Adjust spacing based on the number of players
            for (int k = 0; k < (4 - player_count); k++)
            {
                printf(" "); // Add spaces to align
            }
        }
    }

    printf("\n\n");
    for (int i = 0; i < 20 * 11; i++)
    {
        printf("=");
    }
    printf("\n");

    for (int i = 0; i < numPlayers; i++)
    {
        // Print basic player information
        printf("Player %c:\n", players[i].symbol);
        printf("  Balance: $%d\n", players[i].balance);
        printf("  Current Position: %d (%s)\n", players[i].position, board[players[i].position].name);

        // Print jail status
        if (players[i].inJail)
        {
            printf("  Status: In Jail\n");
        }
        else
        {
            printf("  Status: Not in Jail\n");
        }

        // Print number of rolls and jail visits
        printf("  Total Rolls: %d\n", players[i].rolls);
        printf("  Jail Visits: %d\n", players[i].jailVisits);

        // Calculate average roll value
        if (players[i].rolls > 0) // Avoid division by zero
        {
            float avgRollValue = (float)players[i].sumOfRolls / players[i].rolls;
            printf("  Average Roll Value: %.2f\n", avgRollValue);
        }
        else
        {
            printf("  Average Roll Value: N/A\n");
        }

        // Print owned properties
        int ownedCount = 0;
        int mortgagedCount = 0;
        printf("  Properties Owned/Mortgaged:\n");
        int ownsProperty = 0; // Flag to check if the player owns any property
        for (int j = 0; j < BOARD_SIZE; j++)
        {
            if (players[i].properties[j] == 1) // Property owned
            {
                printf("    - %s (Unmortgaged)\n", board[j].name);
                ownsProperty = 1;
                ownedCount++;
            }
            else if (players[i].properties[j] == -1) // Property mortgaged
            {
                printf("    - %s (Mortgaged)\n", board[j].name);
                ownsProperty = 1;
                mortgagedCount++;
            }
        }
        if (ownedCount == 0 && mortgagedCount == 0)
        {
            printf("    None\n");
        }
        else
        {
            printf("  Total Owned Properties: %d\n", ownedCount);
            printf("  Total Mortgaged Properties: %d\n", mortgagedCount);
        }

        printf("\n");
    }
}

// Main Function
int main()
{
    srand(time(NULL));

    initializeBoard();
    initializePlayers();
    printBoard();

    int round = 0;
    while (!checkGameEnd())
    {
        printf("\n--- Round %d ---\n", ++round);
        for (int i = 0; i < numPlayers; i++)
        {
            playerTurn(i);
            printBoard();
        }
    }

    // Determine the winner: the player with the highest balance and still has non-mortgaged properties
    int winnerIndex = -1;
    int maxBalance = -1;

    for (int i = 0; i < numPlayers; i++)
    {
        // Check if the player has at least one non-mortgaged property
        int hasNonMortgagedProperties = 0;
        for (int j = 0; j < BOARD_SIZE; j++)
        {
            if (players[i].properties[j] == 1) // Owned and not mortgaged
            {
                hasNonMortgagedProperties = 1;
                break;
            }
        }

        // If the player has non-mortgaged properties and has the highest balance, they win
        if (hasNonMortgagedProperties && players[i].balance > maxBalance)
        {
            maxBalance = players[i].balance;
            winnerIndex = i;
        }
    }

    // Output the winner
    if (winnerIndex != -1)
    {
        printf("\nGame Over!\n");
        printf("Player %c wins with $%d and still has non-mortgaged properties!\n", players[winnerIndex].symbol, maxBalance);
    }
    else
    {
        printf("\nGame Over! No one has properties left to mortgage.\n");
    }
    return 0;
}
