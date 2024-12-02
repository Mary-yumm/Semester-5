#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>

int main()
{
    char server_msg[200];
    char buff[200];

    //creating socket
    int server_sock = socket(AF_INET,SOCK_STREAM,0);
    //setup address
    struct sockaddr_in server_address;
    server_address.sin_family = AF_INET;
    server_address.sin_port = htons(3001);
    server_address.sin_addr.s_addr  = INADDR_ANY;

    //binding
    bind(server_sock,(struct sockaddr *)&server_address,sizeof(server_address));

    //listen
    listen(server_sock,5);
    printf("Server is listening...\n");

    while(1){
        //accept
        int client_accept = accept(server_sock,NULL,NULL);
        pid_t id = fork();
        if(id ==0){
            while(1){
                recv(client_accept, buff, sizeof(buff),0);
                if(strcmp(buff,"bye\n")==0){
                    printf("bye");
                    close(client_accept);
                    exit(0);
                }
                printf("Client:%s \n",buff);
                printf("Server: ");
                fgets(server_msg,sizeof(server_msg),stdin);
                send(client_accept,server_msg,sizeof(server_msg),0);
            }
        }
        else{
            close(client_accept);
    
        }
    }

    
}