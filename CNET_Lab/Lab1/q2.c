#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>

// CLIENT
int main()
{
    char client_msg[200] = "What is your name?";
    char buff[200];

    // Creating a socket
    int client_sock = socket(AF_INET, SOCK_STREAM, 0);

    struct sockaddr_in client_address;
    client_address.sin_family = AF_INET;
    client_address.sin_port = htons(3001);
    client_address.sin_addr.s_addr = INADDR_ANY;

    // Connecting to the server
    connect(client_sock, (struct sockaddr *)&client_address, sizeof(client_address));

    // Client prints the message
   // printf("%s\n", client_msg);

    // Sending message to server
    send(client_sock, client_msg, sizeof(client_msg), 0);

    // Receiving server's name
    recv(client_sock, buff, sizeof(buff), 0);

    // Printing the server's name
    printf("Hi %s\n", buff);

    close(client_sock);

    return 0;
}
