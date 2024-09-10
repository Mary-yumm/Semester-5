#include<stdio.h>
#include<stdlib.h>
#include<sys/socket.h>
#include<sys/types.h>
#include<netinet/in.h>
#include<string.h>
#include<unistd.h>
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
