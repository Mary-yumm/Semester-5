#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <time.h>

int main()
{
    char server_msg[200];
    char buffer[200];
    char choice[3] = {'r', 'p', 's'};
    srand(time(NULL));  // Seed the random number generator

    // Create socket
    int server_sock = socket(AF_INET, SOCK_STREAM, 0);
    if (server_sock < 0)
    {
        perror("Failed to create socket");
        exit(1);
    }

    // Setup address
    struct sockaddr_in server_address;
    server_address.sin_family = AF_INET;
    server_address.sin_port = htons(3001);
    server_address.sin_addr.s_addr = INADDR_ANY;

    // Bind socket
    if (bind(server_sock, (struct sockaddr *)&server_address, sizeof(server_address)) < 0)
    {
        perror("Failed to bind");
        exit(1);
    }

    // Listen for incoming connections
    listen(server_sock, 5);
    printf("Server is listening...\n");

    // Accept a single client
    int client_accept = accept(server_sock, NULL, NULL);
    if (client_accept < 0)
    {
        perror("Failed to accept connection");
        exit(1);
    }

    while (1)
    {
        // Receive client's choice
        recv(client_accept, buffer, sizeof(buffer), 0);

        if (buffer[0] == 'e')  // Exit condition
        {
            printf("Client exited the game\n");
            break;
        }

        int randomIndex = rand() % 3;  // Randomly choose server's move
        printf("Client's Choice: %c\n", buffer[0]);
        printf("Server's Choice: %c\n", choice[randomIndex]);

        int server_choice = choice[randomIndex];

        // Determine winner
        if (buffer[0] == server_choice)
            server_msg[0] = 't';  // Tie
        else if ((buffer[0] == 'r' && server_choice == 's') ||
                 (buffer[0] == 'p' && server_choice == 'r') ||
                 (buffer[0] == 's' && server_choice == 'p'))
            server_msg[0] = 'c';  // Client wins
        else
            server_msg[0] = 's';  // Server wins

        // Send the result to the client
        send(client_accept, server_msg, sizeof(server_msg), 0);
    }

    close(client_accept);  // Close the client socket
    close(server_sock);    // Close the server socket
    return 0;
}
