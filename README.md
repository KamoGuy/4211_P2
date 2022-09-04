
# Kamolchai Hang, Prof. Zhang CSCI4211,14/11/2021  
# Java,simulation.java,simulation.class

## Compilation

1. Make sure to have a Java SDK installed

1. Open a terminal/CLI

2. Navigate to the src directory of the protocol you want to test

3. Complie by issing this command: javac simulator.java

## Execution/Running

1. Navigate to the src directory of the protocol you want to test using a terminal/CLI

2. Make sure to complie before running, look at Compliation Section

3. To run, use this command: java simulator

4. To test different cases, open the simulator.java with an editor

5. Change the ratios/probability of packet corruption and packet drop with the variable 'corruptprob' and 'lossprob' repectively

6. To have more/less messages, change 'nsimmax'

7. Change 'Lambda' if needed for the result wanted

8. Save the file and recomplie to run the changes

## Description
For both protocol, I add a global boolean for intial timer. It just help me wrap my head around the 1 timer at a time condition.

### Alternating bit/ Stop and Wait
Starting out, the A class has four required attributes: state, seq, estimated_rtt, and lastpacket. state is use to determine when A can start getting a packet transmitting. seq is used to keep track of what sequence of packets being transmitted. estimated_rrt is the max wait time before retransmission, idicating a possiblity of a packet loss. lastpacket is used to store the packet that was previously transmitted for retransmission if needed. 

In A_input method/function, I implace a few measure for data intergrity. I created a for loop to check each element of the payload for data integrity. I also check the checksum calculation. And lastly the the ACK number before changing A's state to 'WAIT_LAYERS' and updating seq to the next number. Following the guidelines in the guides folder, I also add  retransmitting the packet if the conditions are not meet. 

In A_output, the method creates a new packet with the current sequence number seq, and the message m. It then sends the packet over to layer 3 to start transmitting. The medthod also start the timer for that transmission. Next, the method will save the packet that was just sended to lastpacket incase there needs to be a retransmission of that packet. Lastly, the method will change A's state to "WAIT_ACK" to to wait for an acknowledgement from B.

A_handle_timer will retransmit the packet stored in lastpacket to layer 3 and reset the timer.

In the B class, it only needs to keep track of the sequence number, seq. The only method that really to be implemented is B_input. Like A_input, I also use a for loop to check for data integrity. I also check the checksum computaion and seqeunce number with the expected sequence number before sending stuff to layer 5 and sending an ACK back to A. In this method, I included a retransmssion of ACK if it recieve a the previous sequence number. This will occur if the ACK packet was lost or there is corruption in it. 

### Go Back N
For the most part, my implementation of this protocol is very similar to Stop and Wait. The only differences is how this protocol keep track of transmitted packets, which is through a circular buffer (also known as a window) and there is no state attribute and lastpacket. 

In A_input, when it receive a correct ACK from B, it will pop/remove that packet from the buffer c_b. As, stated the implementation is pretty much the same as in Stop and Wait, but without the state changes.

Unlike Stop and Wait, the all retransmissions are handled in A_handle_timer. In A_handle_timer, I create a for loop to restransmit all the packet in the buffer. I then reset timer. 

In A_output, if the buffer is full, ignore the message and don't create a packet for transmission. Otherwise, create a packet and send it to layer 3 like in Stop and Wait. This time save the packet to the buffer instead.

## Evaluation
Look at the pictures in the specific protocol folder. I have 3 diffrent picture of the outcomes for each protocol. The ratio or probability for 'lossprob' and 'corruptprob' are in the name of the pictures. Also, how many message to expect before stopping and the Lambda used are in the name. 

For Stop and wait, it will only transmit 1 packet at a time. It will not transmit a new packet until an Ack is received. If a packet was corrupted, B will not process it and there will be a time out. In which A will retransmit the packet to B until it receive an uncorrupted packet. If an ACK lost from B to A, after timeout, A will restranmit the packet to B. B will recieve this packet and notice it has already process that packet and retransmit the ACK to A.

Go Back N works with a buffer or window for transmitting from A to B. I set my window to 8 as stated in the rubic/guideine. A will keep transmitting packets until the buffer is full. B will only ACK packets in sequetial order. Even though B might recive packets out of order, B will not send an ACK for them. B will only Ack the expected packet in order. If B recive a corroupted packet it will ignore it as well. When timeout occur, A will retransmit all the packet in the buffer. Once B process a uncorrupted/correct packet it will sent an ACK to A. If the ACK was lost on the way, After timeout, A will retransmit all the packets in the buffer. B will receive a packet that it has already process, and retransmit the ACK to A. 
