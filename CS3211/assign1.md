1) 

Here, I implemented the sensor as a process accepting level values (starting with 5, then from 0-9) from a channel then printing out appropriate level statuses. 

	chan level = [0] of {int};
	proctype Sensor (chan levelChan) {
		int x;
		do
		:: (true) ->
				levelChan ? x;
				if 
				:: (x < 2) -> printf("low %d \n", x);
				:: (x > 8) -> printf("high %d \n", x);
				:: else -> printf("normal %d \n", x);
				fi
		od
	}
	init {
		int lvl = 5;
		run Sensor(level);	
		level ! lvl;
		lvl = 0;
		do 
		:: (true) ->
			if
			:: (lvl > 9) -> break;
			:: else -> 
				level ! lvl; 
				lvl++;
			fi
		od
	}

2) 

Here, I implemented the user as a process paying for a drink using 5, 10 or 20 cents (randomly) until the machine returns a drink & change.  

	chan moneyIn = [0] of {int};
	chan drinkOut = [0] of {bit};
	chan changeOut = [0] of {int};

	proctype USER(chan payment, drink, change) {
		bit done;
		int pay, paid, money;

		paid = 0;

		do
		:: (true) -> 
			if 
			:: pay = 5; 
			:: pay = 10;
			:: pay = 20;
			fi;

			paid = paid + pay;
			payment ! pay;

			drink ? done;
			if
			:: (done) -> break;
			:: else -> skip;
			fi;
		od;
		change ? money;
		printf("paid %d, got drink and %d change", paid, money);
	}

	proctype MACHINE(chan payment, drink, change) {
		int received, totalRecv;
		totalRecv = 0;

		do 
			:: (true) -> 
				payment ? received;
				totalRecv = totalRecv + received;
				if
				:: (totalRecv >= 15) ->
					drink ! 1;
					change ! (totalRecv - 15);
					break;
				:: else ->
					drink ! 0;
				fi;
		od;
	}

	init {
		run USER(moneyIn, drinkOut, changeOut);
		run MACHINE(moneyIn, drinkOut, changeOut);
	}

3. 

	CLIENT = call -> (wait / timeout) -> continue -> CLIENT
	SERVER = request -> (busy / (service -> reply)) -> SERVER
	||CLIENTS_SERVER = (c1:CLIENT || c2:CLIENT || {c1, c2}::SERVER)
		/{call/request, reply/wait, busy/timeout}
	
I think that it’s reasonable for the server to timeout/reject more requests if it’s too busy, especially in a system where the server is serving many clients. Without this mechanism, clients might be waiting on a non-responsive server. 