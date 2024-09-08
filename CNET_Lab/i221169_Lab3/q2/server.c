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

    int len = sizeof(client_address);
    
    int size_msg = recvfrom(server_socket,buff,sizeof(buff),0,(struct sockaddr*)&client_address,&len);
    int i=0;
    int count=0;
    while(buff[i]!='\0'){
        if(buff[i]==' '){
            count++;
        }
        i++;
    }
    printf("No of Words: %d\n",count+1);
    //printf("Client's message: %s\n",buff);

    close(server_socket);

}
