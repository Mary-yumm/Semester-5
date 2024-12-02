#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <string.h>
#include <unistd.h>

int main()
{
    char server_msg[200];
    char buff[200];

    // creating socket
    int server_sock = socket(AF_INET, SOCK_STREAM, 0);

    // setup address
    struct sockaddr_in server_address;
    server_address.sin_family = AF_INET;
    server_address.sin_addr.s_addr = INADDR_ANY;
    server_address.sin_port = htons(3001);

    // binding
    bind(server_sock, (struct sockaddr *)&server_address, sizeof(server_address));

    // listen
    listen(server_sock, 5);
    printf("Server is listening...\n");

    while (1)
    {
        // Accept client connection
        int client_accept = accept(server_sock, NULL, NULL);

        if (client_accept < 0) {
            perror("Failed to accept connection");
            continue;
        }

        // Create a child process to handle the client
        pid_t id = fork();

        if (id == 0) {  // Child process
           // close(server_sock);  // Close listening socket in child

            while (1)
            {
                // Receive message from client
                recv(client_accept, buff, sizeof(buff), 0);

                // Check for "bye" to exit
                if (strcmp(buff, "bye\n") == 0) {
                    printf("Client sent 'bye', closing connection...\n");
                    close(client_accept);
                    exit(0);  // Terminate child process for this client
                }

                printf("Client: %s\n", buff);

                // Get server response
                printf("Server: ");
                fgets(server_msg, sizeof(server_msg), stdin);

                // Send server response to client
                send(client_accept, server_msg, sizeof(server_msg), 0);
            }
        }
        else {
            // Parent process: close the accepted socket and continue accepting other clients
            close(client_accept);
        }
    }

    close(server_sock);
    return 0;
}
