Drexel Flight Mainpage:

https://drexelflight.wordpress.com/

Drexel Flight Debut Video:

https://www.youtube.com/watch?v=wdQQE7ITN3s

The motion simulator software sent and received packets of information representing the movements of the motion simulator.  To decipher what the packets of information meant, we set up a 3rd party server to read its packets & monitor its activity.  By writing out the information & recording the data, along with following it using wireshark, we were able to figure out the structures of said packets.

The motion simulator itself, if instructed to receieve information rather than to run the original safari simulation, must be sent a constant stream of packets.  Our server defines a client which sends a stream of "stationary" packets which keep the ride at "point 0", or at the starting point.  If our server receieves a signal from the Unity game client, it then utilizes those packets instead.

On the game client, we defined a base Wrapper class to communicate with the server using a custom Client class, then extended the Wrapper class to meet the needs of Drexel Flight by basing the movements of the motion simulator on a rigidbody in Unity.  The Wrapper could be extended to utilize other means of moving around the motion simulator in the future.