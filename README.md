# JIT
git but writtern in java!

ok, to start of with we will have to dip into how command line works i..e. when you type
into the command line how does it know what to do? where is the functionality of the 
command line even stored? the short answer is when we type a command in the terminal, 
the shell looks for a program file and asks the os kernel to run the command. 

so now i want to define a few terms so that we their is no confusion. A Terminal is 
simply a text input/output window. The shell is one Interpreting your command similar
to what python and other interpreted languages do. Finally the kernal is the 
OS core that runs the program for us. 

So for example, lets say i typed "ls" in the command line. What the terminal does 
is that it captures keystrokes. now what the terminal does is that it sends the string 
"ls\n" to the shell. remember that the terminal is kinda just reads what you typed and 
gives it to the shell. it does not have any logic of its own.

now once the string is given to the shell, some commands are writtern inside the shell
itself like cd, exit, export, alias etc. these must be built ins because they modify
the shells state. if the typed command is part of the commands that are buildin wihtin
the shell, then then the shell runs the internal code associated with the command.
Else we continue our journey.

Then it checks if the function is an alias function or not. eg: alias ll = "ls -la".
The shell expands this and repeats parsing. alias is just text replacement. so when 
the shell sees "ll" now it just repalces it with "ls -la".

so if its neither of these cases, the shell starts hunting for the file associated 
with the command. the shell will look in the directories located in $PATH. the path 
really depends on your os but lets say for mac its for eg : 
                        /usr/local/bin:/usr/bin:/bin

so for a command eg "ls" it checks the files in one of these locations : 

                                /usr/local/bin/ls
                                /usr/bin/ls   ← FOUND

then when it finds it the shell will make a system call to the kernel to run the 
file. eg: for "ls" its gonna be execve("/bin/ls", ["ls"], env). 

the kernel takes over and executes its program and flows the output back to terminal.
