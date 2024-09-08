#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <string.h>

int main()
{
    char client_msg[200] = "Hi";
    char buf[200];

    // Creating a socket
    int client_sock_fd = socket(AF_INET, SOCK_STREAM, 0);

    // Struct to store the address and port (where we will connect the socket)
    struct sockaddr_in server_address;

    server_address.sin_family = AF_INET;
    server_address.sin_port = htons(3002);
    server_address.sin_addr.s_addr = INADDR_ANY;

    // Request for connection -> Connect() ONLY DONE ONCE
    connect(client_sock_fd, (struct sockaddr *)&server_address, sizeof(server_address));
    printf("Connected to server\n");

    char input[200];
    printf("Enter your message: ");
    scanf("%s", input);

    send(client_sock_fd, input, strlen(input) + 1, 0);
    printf("Message sent to server\n");

    recv(client_sock_fd, buf, sizeof(buf), 0);
    printf("Message received from server: %s \n", buf);

    // Close the socket
    close(client_sock_fd);

    return 0;
}
