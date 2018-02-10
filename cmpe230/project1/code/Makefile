all:	stm2ir

stm2ir:		main.o Node.o Tokenizer.o ErrorCheckerAndTreeGenerator.o LLVMCodeGenerator.o
			g++ -std=c++11 main.o Node.o Tokenizer.o ErrorCheckerAndTreeGenerator.o LLVMCodeGenerator.o -o stm2ir

main.o:		main.cpp Node.h Tokenizer.h ErrorCheckerAndTreeGenerator.h LLVMCodeGenerator.h
			g++ -std=c++11 -c main.cpp -o main.o

Node.o:		Node.cpp Node.h
			g++ -std=c++11 -c Node.cpp -o Node.o

Tokenzier.o:	Tokenizer.cpp Tokenizer.h
				g++ -std=c++11 -c Tokenizer.cpp -o Tokenizer.o

ErrorCheckerAndTreeGenerator.o:		ErrorCheckerAndTreeGenerator.cpp ErrorCheckerAndTreeGenerator.cpp
									g++ -std=c++11 -c ErrorCheckerAndTreeGenerator.cpp -o ErrorCheckerAndTreeGenerator.o
									
LLVMCodeGenerator.o:	LLVMCodeGenerator.cpp LLVMCodeGenerator.h
						g++ -std=c++11 -c LLVMCodeGenerator.cpp -o LLVMCodeGenerator.o
									
run:	stm2ir
		./stm2ir ${ARGS}