#include<stdio.h>
#include<stdlib.h>
#include<sys/socket.h>
#include<sys/types.h>
#include<netinet/in.h>
#include<string.h>
int main(){
    char server_msg[200]="Hey Client!";
    char buff[200];
    //creating socket
    int server_socket=socket(AF_INET,SOCK_DGRAM,0);

    //setup address
    struct sockaddr_in server_address,client_address;
    server_address.sin_family = AF_INET;
    server_address.sin_addr.s_addr = INADDR_ANY;
    server_address.sin_port = htons(3001);

    //binding
    bind(server_socket, (struct sockaddr*)&server_address,sizeof(server_address));

    while(1){
    int len = sizeof(client_address);
    
    int size_msg = recvfrom(server_socket,buff,sizeof(buff),0,(struct sockaddr*)&client_address,&len);
    if(strcmp(buff,"bye\n")==0){
        printf("tata\n");
        break;
    }
    printf("Size: %d\n",size_msg);
    //printf("Client's message: %s\n",buff);
    

    fgets(server_msg,sizeof(server_msg),stdin);
    
    sendto(server_socket,server_msg,strlen(server_msg),0,(struct sockaddr*)&client_address,len);
    }
    close(server_socket);

}

/*
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
    server_address.sin_port = htons(3001); // port number should be same for both pcs

    // binding
    bind(server_sock, (struct sockaddr *)&server_address, sizeof(server_address));

    // listen
    listen(server_sock, 5);
    int count = 0;
    //int client_accept[3];
    pid_t id;
    int client_accept1;
    int client_accept2;
    int client_accept3;

    while (1)
    {
        // for (int count = 0; count < 3; count++)
        // {
        //     client_accept[count] = accept(server_sock, NULL, NULL);
        //     id = fork();
        // }
        client_accept1 = accept(server_sock, NULL, NULL);
        client_accept2 = accept(server_sock, NULL, NULL);
        client_accept3 = accept(server_sock, NULL, NULL);
        // receive
        id = fork();
        if (id == 0)
        {

            while (1)
            {
                
                recv(client_accept1, buff, sizeof(buff), 0);
                send(client_accept2, buff, sizeof(buff), 0);
                send(client_accept3, buff, sizeof(buff), 0);

                recv(client_accept2, buff, sizeof(buff), 0);
                send(client_accept1, buff, sizeof(buff), 0);
                send(client_accept3, buff, sizeof(buff), 0);

                recv(client_accept3, buff, sizeof(buff), 0);
                send(client_accept1, buff, sizeof(buff), 0);
                send(client_accept2, buff, sizeof(buff), 0);
                
                if (strcmp(buff, "bye\n") == 0)
                {
                    printf("tata\n");
                    break;
                }
                printf("\n %s ", buff);
                //fgets(server_msg, sizeof(server_msg), stdin);

                // for (int i = 0; i < 3; i++)
                // {
                //     send(client_accept[i], server_msg, sizeof(server_msg), 0);
                // }
            }
        }
    }
    close(server_sock);
}

*/
