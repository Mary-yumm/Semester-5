#include<stdio.h>
#include<stdlib.h>
#include<sys/socket.h>
#include<sys/types.h>
#include<netinet/in.h>
#include<string.h>
int main(){
    char server_msg[200]="HI I AM SERVER";
    char buff[200];
    //creating socket
    int server_sock=socket(AF_INET,SOCK_DGRAM,0);

    //setup address
    struct sockaddr_in server_address, client_address;
    server_address.sin_family = AF_INET;
    server_address.sin_addr.s_addr = INADDR_ANY;
    server_address.sin_port = htons(3001);

    //binding
    bind(server_sock, (struct sockaddr*)&server_address,sizeof(server_address));
    //receive from
    while(1){
    
    int len=sizeof(client_address);
    int s=recvfrom(server_sock,buff,sizeof(buff),0,(struct sockaddr*)&client_address,&len);
    printf("Client's:%s\n", buff);
    printf("Size is: %d\n",s);
    if(!strcmp(buff,"quit")){
    sendto(server_sock,buff,sizeof(buff),0,(struct sockaddr*)&client_address,len);
    break;
    }
    scanf("%s", server_msg);
    sendto(server_sock,server_msg,sizeof(server_msg),0,(struct sockaddr*)&client_address,len);
    }
    close(server_sock);

}
