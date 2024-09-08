#include<stdio.h>
#include<stdlib.h>
#include<sys/socket.h>
#include<sys/types.h>
#include<netinet/in.h>
#include<string.h>
int main(){
    char client_msg[200]="Hey! I AM CLIENT";
    char buff[200];
        //creating socket
    int client_sock=socket(AF_INET,SOCK_DGRAM,0);

    //setup address
    struct sockaddr_in server_address;
    server_address.sin_family = AF_INET;
    server_address.sin_addr.s_addr = INADDR_ANY;
    server_address.sin_port = htons(3001);
    int len=sizeof(server_address);
    fgets(client_msg,200,stdin);
    sendto(client_sock,client_msg,sizeof(client_msg),0,(struct sockaddr*)&server_address,len);
    
   close(client_sock);
}
