/** CIS542: Fancy Cooler: C Server
Yayang Tian, Xiao Zhang, Jiehua Zhu, Tianming Zheng

This class acts as a middleware between Android client and chip hardware.
it receives command from Android client and tell chip to update temperature;
It also receives status signals from Arduino board and informs Android client.
*/

#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <termios.h>
#include <fcntl.h>
#include <time.h>

void *readArduino(void *arg);
void *readClient(void *arg);


/** store information of Arduino in order to stop/restart system */
struct ArduinoStatus{
    int planId;
    int stopTemperature;
    int durationTime;
    int fanSpeed;
    int isRunning;
    int startTime;
    int timeLeft;
};

struct Sockets{
    int clientSocket;
    int arduinoSocket;
};

struct ArduinoStatus status={1, 100, 100, 0, -0, 0, 0};
struct ArduinoStatus tempStatus={1, 100, 100, 0, 0, 0, 0};
int tempSpeed;
int stopTime;
int tempExist=0;
int run=0;

/**
*/
//e.g:   s0 26 30 85
//e.g:   s30 -2 -1 30
//e.g:   0 70 15 72
//e.g:   q
int updateStatus ( struct ArduinoStatus * currentStatus,  char str[] )
{

    printf("Parsing status...... \n");
    char *nextToken;
    nextToken = strtok(str, " ");
    while(nextToken!=NULL)
    {
        printf("The next token is %s \n", nextToken);
        char cmd = nextToken[0];

        switch(cmd){

            //Set the status
            case('s'):
            {
                printf("Found command identifier.");
                char charId[2]; charId[0]=nextToken[1]; charId[1]='\0';

                int id = atoi(charId);
                int temperature = atoi( strtok(NULL, " ") );
                int duration = atoi( strtok(NULL, " ") );
                int speed = atoi( strtok(NULL, " ") );
                //char *buf2="lr\0";
                //write(arduino, buf2, strlen(buf2));
                //if( 0<id<9 && 0<temperature<100 && 0<duration && 0<speed<100)
                //    {
                    currentStatus -> planId=id;
                    currentStatus -> stopTemperature=temperature;

                    // write(arduino, buf3, strlen(buf3)-1);ture;
                    currentStatus -> durationTime=duration;
                    currentStatus -> fanSpeed=speed;
                    currentStatus -> isRunning=1;
                    currentStatus -> startTime=time(NULL);
                    currentStatus -> timeLeft= duration;
                    run=0;
                    printf("Setting is Running=  %d", status.isRunning);
                    //Received cmd s, and successfully parsed
                    return 1;

                //} s0 70 30 85
                //Received cmd s, but invalid
                // else return 0;
            }
            break;

            // Abort the Arduino, and remember the status
            case('a'):
            {
                currentStatus -> isRunning = 0;
                return 2;
            }
            break;

            //Query the status
            case ('q'):
            {
                return 3;
            }
            break;

            //Change the fan's speed
            case('f'):
            {
                // char charSpeed[2]; charSpeed[0]=nextToken[1]; charSpeed[1]='\0';
                // int speed=atoi(charSpeed);
                // currentStatus -> fanSpeed=speed;
                printf("Swinging the phone! Changing the speed of fans!");
                return 4;
            }
            break;
        }

        nextToken = strtok(NULL, " ");
    }
    return -1;
}


/**
* This starts the C Server
*/
int start_server(int PORT_NUMBER)
{
    //int bytes_received0 70 30 85
    //char  recv_data[100];
    pthread_t threadArduino, threadClient;

    // structs to represent the server and client
    struct sockaddr_in server_addr,client_addr;

    int sock; // socket descriptor


    if ((sock = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
        perror("Socket");
        exit(1);
    }
    int temp;
    if (setsockopt(sock,SOL_SOCKET,SO_REUSEADDR,&temp,sizeof(int)) == -1) {
        perror("Setsockopt");
        exit(1);
    }

    // configure the server
    server_addr.sin_port = htons(PORT_NUMBER); // specify port number
    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = INADDR_ANY;
    char buf2[50]="l5\0";
    bzero(&(server_addr.sin_zero),8);

    // 2. bind: use the socket and associate it0 70 15 72with the port number
    if (bind(sock, (struct sockaddr *)&server_addr, sizeof(struct sockaddr)) == -1) {
        perror("Unable to bind");
        exit(1);
    }

    // 3. listen: indicates that we want to listn to the port to which we bound; second arg is number of allowed connections
    if (listen(sock, 5) == -1) {
        perror("Listen");
        exit(1);
    }

    printf("\nServer waiting for connection on port %d\n", PORT_NUMBER);
    fflush(stdout);


    // 4. accept: wait until we get a connection on that port
    int sin_size = sizeof(struct sockaddr_in);
    int fd;

    fd = accept(sock, (struct sockaddr *)&client_addr,(socklen_t *)&sin_size);
    printf("Server got a connection from (%s, %d)\n", inet_ntoa(client_addr.sin_addr),ntohs(client_addr.sin_port));

    // buffer to read data into
    char rd[1024];


    //printf("Server received message: %s\n", recv_data);

    int arduino = open("/dev/ttyACM0", O_RDWR);

    // first, open the connection

    if (arduino == -1) {printf("Unable to connect to Arduino! Try to exchange ttyACM0 and ttyACM1");  return;}

    // then configure itatus.durationTime=timeLeft;

    struct termios options;
    tcgetattr(arduino, &options);
    cfsetispeed(&options, 9600);
    cfsetospeed(&options, 9600);
    tcsetattr(arduino, TCSANOW, &options);

    char buf[6];int len;
    //@initialize
    //  planId=1
    //  stopTemperature=100 degree
    //  durationTime= 100 seconds
    //  fanSpeed=0
    //  startTime=0
    //  isRunning=-1
    struct Sockets sockets={fd, arduino};

    //print("fd and arduino equal0 50 5 52 to: %d,%d",fd, arduino);

    pthread_create(&threadArduino, NULL,readArduino, (void*)(&sockets));
    pthread_create(&threadClient, NULL,readClient, (void*)(&sockets));

    pthread_join(threadArduino, NULL);
    pthread_join(threadClient, NULL);

    /*
    // 7. close: close the socket connection
    printf("terminate\n");
    close(far sendingBuf[100];
    while(1){

        sleep(1);
        bytes_received = read(arduino, recv_data, 100);  //read temperature from Arduino
        write(1, recv_data, bprintf(buf2,"l%d", tfan);
        buf2[2]='\0';
        // buf2="l9";
        write(arduino, buf2, strlen(buf2));
        tes_received);

        if(bytes_received > 0)
        {
            //printf("\nByte Recevied:%d\n",bytes_received);
            write(1, recv_data, bytes_received);             //print temperature at C server
            // send(fd, recv_data, bytes_received, 0);

            char *nextToken;
            nextToken=strtok(recv_data, " \n:;C");
            int receivedOne=0;
            while(nextToken!=NULL){
                );
                close(sock);
                close(arduino);
                printf("Server closed connection\n");
                exit(0);
                return 0;
                */

}

            /**
*  This read periodical result from Arduino
*/
void *readArduino(void *arg){
    struct Sockets socketStruct=*(struct Sockets*)arg;
    int fd = socketStruct.clientSocket;
    int arduino = socketStruct.arduinoSocket;
    printf("reading result from arduino: %d,%d",fd, arduino);
    int bytes_received;
    int stime=0;

    char  recv_data[100];
    char sendingBuf[100];
    while(1){

        sleep(1);
        bytes_received = read(arduino, recv_data, 100);  //read temperature from Arduino
        // write(1, recv_data, bytes_received);

        if(bytes_received>0)
        {
            //printf("\nByte Recevied:%d\n",bytes_received);
            write(1, recv_data, bytes_received);             //print temperature at C server
            // send(fd, recv_data, bytes_received, 0);
            int timeLeft;
            char *nextToken;
            nextToken=strtok(recv_data, " \n:;C");
            int receivedOne=0;
            while(nextToken!=NULL){

                //printf("The next token is %s \n", nextToken);

                if( strcmp(nextToken,"EXIST")==0  && receivedOne==0)
                {
                    receivedOne=1;

                    //printf("\nFound EXIST and TEMPERATURE from Arduino!!!");

                    //@argu   exist
                    int exist=atoi( strtok(NULL, ":;") ); //printf("   exist= %d", exist);

                    strtok(NULL, ":;");
                    //@argu    temperature
                    double currentTemperature=atof( strtok(NULL, "\n:;C") );  // printf("    temperature= %f", currentTemperature);
                    //////
                    if(currentTemperature<= status.stopTemperature){
                        status.isRunning=0;  run=0;
                        char *msg2;
                        msg2="l0\0";
                        write(arduino, msg2, strlen(msg2));
                        sleep(1);
                        char *buf2="lg\0";
                        write(arduino, buf2, strlen(buf2));
                        // char buf3[50]="l0\0";
                        // write(arduino, buf3, strlen(buf3)-1);
                    }

                    //if hasn't started yet, don't count timeLeft
                    if(status.isRunning==0)
                    {   //printf("is Runnnign 0");
                        sprintf(sendingBuf, "t%f %d %d\n\0",currentTemperature, 0 ,exist);
                        send(fd, sendingBuf, strlen(sendingBuf), 0);
                    }
                    else
                    {  //printf("isRunnning==1");
                        //@argu    timeLeft
                        if(run ==0) {
                            timeLeft=status.durationTime;
                            stime=status.startTime;
                            stopTime=stime;
                            run=1;
                        }
                        //if not exist, just send the info in temp Status
                        if(exist == 0)
                        {  printf("Arrived here:exist=0");
                            //Contains coffee  ->  leave coffee
                            if(tempExist==1){
                                printf("Leave Cup");
                                stopTime = time(NULL);
                                tempExist=0;
                            }
                            sprintf(sendingBuf, "t%f %d %d\n\0",currentTemperature,status.timeLeft ,exist);
                            send(fd, sendingBuf, strlen(sendingBuf), 0);

                        }
                        else if(exist == 1)
                        {
                            //Resume:  Leave coffee  ->  Contains coffee
                            if(tempExist==0){
                                printf("Resume!");
                                timeLeft= timeLeft-(stopTime-stime) ;
                                status.timeLeft=timeLeft;
                                stime=time(NULL);
                                tempExist=1;
                                //  char buf3[50]="l0\0";
                                //  write(arduino, buf3, strlen(buf3)-1);          //status.durationTime=timeLeft;
                            }
                            else{
                                status.timeLeft= timeLeft- ( time(NULL)- stime);
                                //  printf("tiomeLeft= %d", timeLeft);
                            }

                            if(status.timeLeft<=0){

                                //  write();
                                //  if(currentTemperature== status.stopTemperature){
                                    status.isRunning=0;  run=0;
                                    char *msg2;
                                    msg2="l0\0";
                                    write(arduino, msg2, strlen(msg2));
                                // }
                                sleep(1);
                                char *buf2="lg\0";
                                write(arduino, buf2, strlen(buf2));



                            status.timeLeft=0;}
                            //  status.timeLeft= timeLeft;
                            tempStatus=status;
                            sprintf(sendingBuf, "t%f %d %d\n\0",currentTemperature, status.timeLeft ,exist);

                            send(fd, sendingBuf, strlen(sendingBuf), 0);

                        }
                        // tempExist= exist;
                    }
                }
                nextToken=strtok(NULL, " \n:;C");
            }
        }
    }

}

/**
This receives command from Java Client
*/
void *readClient(void *arg){
    struct Sockets socketStruct=*(struct Sockets*)arg;
    int fd = socketStruct.clientSocket;
    int arduino = socketStruct.arduinoSocket;
    // printf("reading result from arduino: %d,%d",fd, arduino);
    int len;
    char buf[50];
    char *msg = NULL;
    while(len = recv(fd, buf, sizeof(buf), 0)){

        buf[len]='\0';
        //  int parsed = updateStatus(&status, buf);
        //             printf("parsedId is: %d\n", parsed);

        printf("Received sth from Client, which is as follows: \n");
        write(1, buf, len);                               //print temperature at C server
        // write(arduino, buf, strlen(buf)-1);               //write the command to Arduino

        //parsing buf-string received from client, like  "s2 50 1000 80"
        int parsed = updateStatus(&status, buf);
        printf("parsedId is: %d\n", parsed);

        switch(parsed){
            //Set the initial temperature and stopTime
            case 1:
            {
                // char *buf3="lr\0";
                // write(arduino, buf3, strlen(buf3));
                // sleep(1);
                printf("Successfully parsed command! Valid! Updated status!\n");
                msg="rs1\n\0";

                send(fd, msg, strlen(msg), 0);
                int tfan=status.fanSpeed/10;
                tfan=tfan-1;
                if(tfan<=1){
                    tfan=1;

                }
                // tfan=9;
                char buf2[50];
                sprintf(buf2,"l%d", tfan);
                buf2[2]='\0';
                // buf2="l9";
                write(arduino, buf2, strlen(buf2));

                sleep(1);
                char *buf3="lr\0";
                write(arduino, buf3, strlen(buf3));


            }
            break;
            //Cannot parse
            case 0:
            {
                printf("Successfully parsed command! But it's not valid!\n");
                msg="rs0\n\0";
                send(fd, msg, strlen(msg),0);
            }
            break;
            //No command
            case -1:
            {
                printf("No command found!\n");
                msg="No command found!\0";
                send(fd, msg, strlen(msg),0);
            }
            break;
            //Abort
            case 2:
            {
                printf("Aborting Arduino!\n");
                msg="ra\n\0";
                send(fd, msg, strlen(msg),0);

                char buf2[50]="l0\0";
                write(arduino, buf2, strlen(buf2));
                sleep(1);
                char buf3[50]="lg\0";
                write(arduino, buf3, strlen(buf3));
                tempStatus=status;
                // Implement here!! in Arduino!!
            }
            break;
            //Query
            case 3:
            {
                char message[50];
                sprintf(message, "rq%d %d %d %d %d\n\0", tempStatus.isRunning, tempStatus.planId,
                tempStatus.stopTemperature, tempStatus.durationTime, tempStatus.fanSpeed);
                printf("Querying Status......\n");
                send(fd, message, strlen(message),0);
            }
            break;

            //Swing Fans
            case 4:
            {
                msg="l9\0";
                write(arduino, msg, strlen(msg));
                int tfan1=status.fanSpeed/10;
                printf("fanspeed: %d", status.fanSpeed) ;
                tfan1=tfan1-1;
                if(tfan1<=1){
                    tfan1=1;

                }

                printf("\n\ntfan: %d\n",tfan1);
                sleep(3);
                if(status.isRunning==1){
                    char message[50];
                    sprintf(message, "l%d",tfan1);
                    message[2]='\0';
                    write(arduino, message, strlen(message));
                }
            }

            break;
            default:
            break;
        }

        // fputs("-----Sir, the command received from client is: ",stdout);
        // fputs(buf, stdout);
    }
}



int main(int argc, char *argv[])
{
    // check the number of arguments
    if (argc != 2)
    {
        printf("\nUsage: server [port_number]\n");
        exit(0);
    }

    int PORT_NUMBER = atoi(argv[1]);
    start_server(PORT_NUMBER);
}