#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>

int main()
{
    char client_msg[200];
    char buffer[200];

    int client_sock = socket(AF_INET, SOCK_STREAM, 0);
    if (client_sock < 0)
    {
        perror("Failed to create socket");
        exit(1);
    }

    struct sockaddr_in client_address;
    client_address.sin_family = AF_INET;
    client_address.sin_port = htons(3001);
    client_address.sin_addr.s_addr = INADDR_ANY;

    if (connect(client_sock, (struct sockaddr *)&client_address, sizeof(client_address)) < 0)
    {
        perror("Failed to connect to server");
        exit(1);
    }

    int round = 0;
    while (1)
    {
        round++;
        printf("\nROUND %d\n\n", round);

        while (1)
        {
            printf("Enter 'r' for rock, 'p' for paper, 's' for scissors\n");
            fgets(client_msg, sizeof(client_msg), stdin);
            if (client_msg[0] == 'r' || client_msg[0] == 'p' || client_msg[0] == 's')
            {
                send(client_sock, client_msg, sizeof(client_msg), 0);
                break;
            }
            else if (client_msg[0] == 'e')  // Exit the game
            {
                send(client_sock, client_msg, sizeof(client_msg), 0);
                printf("Exiting the game...\n");
                break;
            }
            else
            {
                printf("Invalid input! Please enter 'r', 'p', or 's' or 'e' to exit.\n");
            }
        }
        if (client_msg[0] == 'e')
        {
            break;
        }

        recv(client_sock, buffer, sizeof(buffer), 0);
        if (buffer[0] == 's')
            printf("Server won!\n");
        else if (buffer[0] == 'c')
            printf("Client won!\n");
        else
            printf("It's a tie!\n");
    }

    close(client_sock);  // Close the client socket
    return 0;
}
