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
char *hello = "Hello from client";
struct sockaddr_in  clientaddr;

 if ( (sockfd = socket(AF_INET, SOCK_STREAM, 0))< 0 ) {   
    perror("socket creation failed");
        exit(EXIT_FAILURE);
    }
clientaddr.sin_family = AF_INET;  
clientaddr.sin_port= htons(PORT);
clientaddr.sin_addr.s_addr= INADDR_ANY;
int n, len;

connect(sockfd, (const struct sockaddr*)&clientaddr,sizeof(clientaddr));
    char * buff;
    printf("enter your name,car number,model color, arrival time and duration");
    fgets(buff,MAXLINE,stdin);
    printf("%s",buff);
    send(sockfd,buff,sizeof(buff),0);
    recv(sockfd,buffer,sizeof(buffer),0);
    
  close(sockfd);  
  return 0;


}