#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>

// SERVER
int main()
{
    char buff[200];

    // Creating a socket
    int server_sock = socket(AF_INET, SOCK_STREAM, 0);

    // Server address
    struct sockaddr_in server_address;
    server_address.sin_family = AF_INET;
    server_address.sin_port = htons(3001);
    server_address.sin_addr.s_addr = INADDR_ANY;

    // Binding the socket
    bind(server_sock, (struct sockaddr *)&server_address, sizeof(server_address));

    // Listening for connections
    listen(server_sock, 5);
    printf("Server is listening...\n");

    // Accepting a client connection
    int accepted_request = accept(server_sock, NULL, NULL);

    // Receiving message from client (this would be "What is your name?")
    recv(accepted_request, buff, sizeof(buff), 0);

    // Server prompts user to input the server's name
    printf("What is your name???\n");
    scanf("%s", buff);

    // Sending server's name to client
    send(accepted_request, buff, sizeof(buff), 0);

    close(server_sock);

    return 0;
}
