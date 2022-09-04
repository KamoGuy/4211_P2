public class A {
    String state;
    int seq;
    int estimated_rtt;
    packet lastpacket;
    boolean timer_started;
    public A(){
        this.state = "WAIT_ACK";
        this.seq = 0;
        this.estimated_rtt = 30;
        this.timer_started = false;         //may not really needed, but helps me know there is only 1 timer at a time.
        return;
    }
    public void A_input(simulator sim, packet p){
        // process the ACK
        
        //check the payload values to be 0 (check for packet corruption)
        for (int i = 0; i<20; i++){
            if ((int) p.payload[i] != 0){  
                return;
            }
        }
        if (p.checksum != this.seq + 1){
            return;
        }
        if (this.seq+1 == p.acknum){
            //System.out.println("recive ack fron b");
            this.state = "WAIT_LAYERS";
            sim.envlist.remove_timer();
            this.seq = this.seq +1;
        }
        //resend data if ACK is wrong & restart timer
        else{
            sim.to_layer_three('A', this.lastpacket);
            sim.envlist.remove_timer();
            sim.envlist.start_timer('A', estimated_rtt);
        }
    }

    public void A_output(simulator sim, msg m){
        packet p = new packet(this.seq, 0, m); 
        
        sim.to_layer_three('A', p);
        //System.out.println(p.checksum);
        //start a timer if one isn't already started, else stop the old one and start a new one.
        if (this.timer_started == false){
            sim.envlist.start_timer('A', estimated_rtt);
            
        }
        else{
            sim.envlist.remove_timer();
            sim.envlist.start_timer('A', estimated_rtt);
        }
        //save p as the last/previous packet
        this.lastpacket = p;
        //change the state 
        this.state = "WAIT_ACK";
        return;
    }
    
    public void A_handle_timer(simulator sim){
        // resend the packet as needed
        sim.to_layer_three('A', this.lastpacket);
        //System.out.println("timeout, packet lost?");
        //set timer again
        if (this.timer_started == true){
            sim.envlist.remove_timer();
            sim.envlist.start_timer('A', estimated_rtt);
        }
        else{
            sim.envlist.start_timer('A', estimated_rtt);
        }
        return;
    }
}
