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
    server_address.sin_port = htons(3002);

    //binding
    bind(server_sock, (struct sockaddr*)&server_address,sizeof(server_address));

    // listen
    listen(server_sock,5);
    
    int client_accept=accept(server_sock, NULL,NULL);
    //receive
    char number[200];
    float temp;
    char type;
    recv(client_accept, buff, sizeof(buff),0);
    int i=0;
    while(buff[i]!='_'){
    number[i]=buff[i];
    i++;
    }
    i++;
    type= buff[i];
    temp=atof(number);//convert
    float C;
    if(type=='C')
    {
    printf("icamehere");
    C=(temp-32)*5/9;
    } 
    else if(type=='F')
    C=1.8* temp+32;
    printf("temperature is %f",C); 
    close(server_sock);

}
