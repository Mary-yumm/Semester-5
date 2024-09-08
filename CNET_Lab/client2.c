#include<stdio.h>
#include<stdlib.h>
#include<sys/socket.h>
#include<sys/types.h>
#include<netinet/in.h>
#include<string.h>
int main(){
    char client_msg[200]="Hey! What is your name";
    char buff[200];
        //creating socket
    int client_sock=socket(AF_INET,SOCK_STREAM,0);

    //setup address
    struct sockaddr_in client_address;
    client_address.sin_family = AF_INET;
    client_address.sin_addr.s_addr = INADDR_ANY;
    client_address.sin_port = htons(3002);
    
   //connect
   connect(client_sock, (struct sockaddr*)&client_address,sizeof(client_address));
  scanf("%s", client_msg);
   send(client_sock,client_msg,sizeof(client_msg),0);
   recv(client_sock, buff, sizeof(buff),0);
   
   close(client_sock);
}
