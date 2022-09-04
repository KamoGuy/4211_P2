public class B {
    int seq;
    public B(){
        this.seq = 0;
    }
    public void B_input(simulator sim,packet pkt){
        // TODO: process the packet recieved from the layer 3

        if (((int)pkt.payload[0]*20)+ seq == pkt.checksum && pkt.seqnum == seq){
            //check the ASCII value of the char to be the same, another test for corruption
            for (int i = 0; i<20; i++){
                if ((int) pkt.payload[i] != (97+seq)){ 
                    return;
                }
            }
            
            sim.to_layer_five('B',pkt.payload);
            seq ++;
            pkt.send_ack(sim, 'B', seq);
           // System.out.println("sending ack from b");
        }

        //resend ACK if there was drop or corrupted ACK packet to A
        if (pkt.seqnum == seq -1){
            pkt.send_ack(sim, 'B', seq);
        }
        return;   
    }
    public void B_output(simulator sim){
        return;
    }
    public void B_handle_timer(simulator sim){
        return;
    }
}
