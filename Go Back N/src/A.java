public class A {
    int seq;
    int estimated_rtt;
    circular_buffer c_b;
    boolean timer_started;
    public A(){
        this.c_b = new circular_buffer(8);
        this.seq = 0;
        this.estimated_rtt = 30;
        this.timer_started = false;         //may not really needed, but helps me know there is only 1 timer at a time.
        return;
    }
    public void A_input(simulator sim, packet p){
        // process the ACK from B
        
        //check the payload values to be 0 (check for packet corruption)
        for (int i = 0; i<20; i++){
            if ((int) p.payload[i] != 0){  
                return;
            }
        }
        if (p.checksum != this.seq + 1){
            return;
        }
        //move the "window" by poping the packet
        if (this.seq+1 == p.acknum){
            //System.out.println("recive ack fron b");
            sim.envlist.remove_timer();
            this.c_b.pop();
            this.seq = this.seq +1;
        }
    }
    public void A_output(simulator sim, msg m){
        // TODO: called from layer 5, pass the data to the other side
        //buffer is full, drop the packet/message
        if (this.c_b.isfull()){
            return;
        }
        packet p = new packet(this.seq, 0, m);
        this.c_b.push(p);
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
        return;
    }
    public void A_handle_timer(simulator sim){
        // TODO: handler for time interrupt
        // resend the packet as needed using read_all() from circular buffer
        //resend all packet in the buffer
        packet[] resend_p = this.c_b.read_all();
        for(int i = 0; i< this.c_b.count; i++){
            sim.to_layer_three('A', resend_p[i]);
        }
        
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