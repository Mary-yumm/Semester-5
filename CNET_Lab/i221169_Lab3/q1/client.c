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