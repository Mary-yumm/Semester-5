#include<stdio.h>
#include<stdlib.h>
#include<unistd.h>
#include<string.h>
#include<sys/types.h>
#include<sys/socket.h> 
#include <arpa/inet.h>
#include<netinet/in.h>
#include<string.h>
#define PORT  1234
#define MAXLINE 1024
int main() {
int sockfd;     
char buffer[MAXLINE]; 
char servermsg[MAXLINE];     
char *hello = "Hello from client";
struct sockaddr_in  servaddr;

 if ( (sockfd = socket(AF_INET, SOCK_STREAM, 0))< 0 ) {   
    perror("socket creation failed");
        exit(EXIT_FAILURE);
    }
servaddr.sin_family = AF_INET;  
servaddr.sin_port= htons(PORT);
servaddr.sin_addr.s_addr= INADDR_ANY;
int n, len;

if ( bind(sockfd, (const struct sockaddr
*)&servaddr,sizeof(servaddr))< 0 )
{
    perror("bind failed");
    exit(EXIT_FAILURE);

}
listen(sockfd,5);
while(1){
    int client1=accept(sockfd,NULL,NULL);   
    int client2=accept(sockfd,NULL,NULL);   
    int client3=accept(sockfd,NULL,NULL);   
    int client4=accept(sockfd,NULL,NULL);   
    int client5=accept(sockfd,NULL,NULL);   
   int pid=fork();
    if(pid==0){
    
    recv(client1,buffer,sizeof(buffer),0);  
    printf("%s",buffer);
    strcat(servermsg,buffer);
    send(client1,hello,sizeof(hello),0);
  
     recv(client2,buffer,sizeof(buffer),0);  
    printf("%s",buffer);
   strcat(servermsg,buffer);
   send(client2,hello,sizeof(hello),0);

     recv(client3,buffer,sizeof(buffer),0);  
    printf("%s",buffer);
    strcat(servermsg,buffer);
    send(client3,hello,sizeof(hello),0);
  
     recv(client4,buffer,sizeof(buffer),0);  
    printf("%s",buffer);
    strcat(servermsg,buffer);
    send(client4,hello,sizeof(hello),0);

    recv(client5,buffer,sizeof(buffer),0);  
    printf("%s",buffer);
    strcat(servermsg,buffer);
    send(client5,hello,sizeof(hello),0);
    
    }
}
  close(sockfd);  
  return 0;
}