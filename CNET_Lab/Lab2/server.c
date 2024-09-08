#include<stdio.h>
#include<stdlib.h>
#include<sys/socket.h>
#include<sys/types.h>
#include<netinet/in.h>
#include<string.h>
int main(){
    char server_msg[200];
    char buff[200];
    //creating socket
    int server_sock=socket(AF_INET,SOCK_STREAM,0);

    //setup address
    struct sockaddr_in server_address;
    server_address.sin_family = AF_INET;
    server_address.sin_addr.s_addr = INADDR_ANY;
    server_address.sin_port = htons(3001);

    //binding
    bind(server_sock, (struct sockaddr*)&server_address,sizeof(server_address));

    // listen
    listen(server_sock,5);
    
    int client_accept=accept(server_sock, NULL,NULL);
    //receive
    while(1){
    recv(client_accept, buff, sizeof(buff),0);
    printf("\n %s \n" , buff);
    if(!strcmp(buff,"exit")){
    send(client_accept,buff,sizeof(buff),0);
    break;
    }
    scanf("%s", server_msg);
    send(client_accept,server_msg,sizeof(server_msg),0);
    }
    close(server_sock);

}
