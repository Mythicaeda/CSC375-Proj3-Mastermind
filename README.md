# CSC375-Proj3-Mastermind
A program that will randomly generate and then solve a game of Mastermind.

Solution in `Parallel` runs on a single machine and uses RecursiveTasks.

Solution in `Cluster` requires a cluster of three machines to run. It will also need the Host and Port field modified in both the Client and Server, as they're hard coded for the school server. Also, don't use the TCP code as reference, I took Parallel before Networking and didn't understand how to properly close a TCP connection at the time, so someone usually ends up crashing during shutdown. I really oughta go back and fix the networking part.

`Client.java` contains the main method in `Cluster/Client`.
