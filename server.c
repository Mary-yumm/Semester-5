#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <string.h>

int main()
{
    char server_msg[200] = "Hi I am server";
    char buf[200];

    // creating a socket
    int server_sock = socket(AF_INET, SOCK_STREAM, 0);

    /*
    Socket has 2 types DGram and Stream
    Dgram = UDP
    Stream = TCP
    */

    // Struct to store the address and port (where we will bind the socket)
    struct sockaddr_in server_address;

    server_address.sin_family = AF_INET;
    server_address.sin_port = htons(3002);
    server_address.sin_addr.s_addr = INADDR_ANY;

    // Binding the socket to the address
    bind(server_sock, (struct sockaddr *)&server_address, sizeof(server_address));

    // Listen for incoming connections
    listen(server_sock, 5); // 5 -> backlog

    // Accept the incoming connection
    int accepted_request = accept(server_sock, NULL, NULL);

    // Receive the input from the client
    recv(accepted_request, buf, sizeof(buf), 0);
    printf("\t\t\t\tMessage received from client: %s \n", buf);

    
    int num = 0;
    int i = 0;
    while (buf[i] >= '0' && buf[i] <= '9') {
        num *= 10;
        num += (buf[i] - '0');
        i++;
    }

    char unit = buf[i];
    char input[200]; // Buffer for the response

    if (unit == 'g') 
    {
        num = (int)(num * 3.785); 
        snprintf(input, sizeof(input), "%d l", num); 
    } 
    else if (unit == 'l') 
    {
        num = (int)(num / 3.785); // Convert liters to gallons
        snprintf(input, sizeof(input), "%d g", num); // Format the result as a string
    } 
    else 
    {
        snprintf(input, sizeof(input), "Invalid unit");
    }

    printf("Converted message: %s \n", input);
    send(accepted_request, input, strlen(input) + 1, 0);
    printf("\t\t\t\tConversion successful, message sent to client\n");

    // Close the socket
    close(server_sock);

    return 0;
}
