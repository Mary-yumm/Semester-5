// udp client
#include<stdio.h>
#include<stdlib.h>
#include<sys/socket.h>
#include<sys/types.h>
#include<netinet/in.h>
#include<string.h>
int main(){
    char client_msg[200]="Hey Server!";
    char buff[200];
        //creating socket
    int client_sock=socket(AF_INET,SOCK_DGRAM,0);

    //setup address
    struct sockaddr_in client_address;
    client_address.sin_family = AF_INET;
    client_address.sin_addr.s_addr = INADDR_ANY;
    client_address.sin_port = htons(3001);
    

    while(1){
    fgets(client_msg,sizeof(client_msg),stdin);
    if(strcmp(client_msg,"bye\n")==0){
        break;
    }
    
    int len = sizeof(client_address);
    sendto(client_sock,client_msg,strlen(client_msg),0,(struct sockaddr*)&client_address,len);

    int size_msg = recvfrom(client_sock,buff,sizeof(buff),0,(struct sockaddr*)&client_address,&len);
    
    printf("Size: %d\n",size_msg);
    //printf("Server's message: %s\n",buff);

    
    //snprintf(buff,sizeof(buff),256);
    }
    close(client_sock);
}

//udp server
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

// tcp client
int main(){
    char client_msg[200]="Hey! What is your name";
    char buff[200];
        //creating socket
    int client_sock=socket(AF_INET,SOCK_STREAM,0);

    //setup address
    struct sockaddr_in client_address;
    client_address.sin_family = AF_INET;
    //client_address.sin_addr.s_addr = in_addr("172.16.49.237"); //arrijs ip (client will write it)
    client_address.sin_addr.s_addr = INADDR_ANY;
    client_address.sin_port = htons(3001);
    
   //connect
   connect(client_sock, (struct sockaddr*)&client_address,sizeof(client_address));
   //send
   while(1){
    printf("\nEnter your message: ");
    fgets(client_msg,sizeof(client_msg),stdin);
    if(strcmp(client_msg,"bye\n")==0){
        break;
    }

    int len = sizeof(client_address);

   send(client_sock,client_msg,sizeof(client_msg),0);
   recv(client_sock, buff, sizeof(buff),0);
 
   printf("\n%s \n" , buff);
   }
   close(client_sock);
   return 0;
}

//tcp server
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
                
            }
        }
    }
    close(server_sock);
}