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
    int len=sizeof(client_address);
    recvfrom(server_sock,buff,sizeof(buff),0,(struct sockaddr*)&client_address,&len);
    printf("Client's:%s\n", buff);
    int i=0;
    int sum=0;
    while(buff[i+1]!='\0'){
    if(buff[i]!=' ')
    sum+=(buff[i]-'0');
    i++;
    }
    printf("Sum is%d",sum);
    close(server_sock);

}
