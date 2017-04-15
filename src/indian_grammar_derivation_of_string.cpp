/*
 * =====================================================================================
 *
 *       Filename:  thread_example.cpp
 *
 *    Description:  
 *
 *        Version:  1.0
 *        Created:  11/14/2015 15:05:19
 *       Revision:  none
 *       Compiler:  gcc/g++
 *
 *         Author:  Marius-Constantin Melemciuc  
 *          email:  
 *   Organization:  
 *
 * =====================================================================================
 */

#include <iostream>
#include <cstdlib>
#include <ctime>
#include <thread>
#include <fstream>
#include <vector>
#include <string>
#include <unordered_map>
#define NUMBER_OF_THREADS 1

using namespace std;

bool isTerminal(char character) {
    if (character >= 'a' && character <= 'z') {
        return true;;
    }
    return false;
}

bool hasOnlyTerminals(string& str) {
    for (int i = 0; i < str.length(); i++) {
        if (!isTerminal(str[i])) {
            return false;
        }
    }
    return true;
}

string globalString;

void callFromThread(int id, char nonterminal, string production) {
    int a, b;
    
    a = 0;
    b = globalString.size() - 1;
    /*
    if (id == 0) {
        a = 0;
        b = globalString.size() >> 1;
    } else {
        a = (globalString.size() >> 1) + 1;
        b = globalString.size() - 1; 
    }
    */
    string str = "";
    for (int i = a; i <= b; i++) {
        if (globalString[i] != nonterminal) {
            str += globalString[i];
        } else {
            str += production;
        }
    } 
    globalString = str;
}

string solve(unordered_map<char, vector<string> >& m, string str) {
    vector<thread> threads;
    char nonterminal;
    vector<string> productions;
    int randomIndex;

    srand(time(NULL));

    while (!hasOnlyTerminals(str)) { 
        nonterminal = '+';
        for (int i = 0; i < str.length(); i++) {
            if (!isTerminal(str[i])) {
                nonterminal = str[i];
                break; 
            }
        }
        if (nonterminal == '+') {
            break;
        }
        //replace all nonterminal appearences
        if (m.find(nonterminal) == m.end()) {
            break;
        }
        productions = m[nonterminal];
        randomIndex = rand() % productions.size();
        string production = productions[randomIndex];
        //cout << "\n" << randomIndex << " " << production << "\n";
        // replace all nonterminal appearences with production string 
        cout << "\nUsing " << nonterminal << " " << production; 
        cout << " we obtain ";
        globalString = str;  
        threads.clear();
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            threads.push_back(thread(callFromThread, i, nonterminal, production));
        }
        for (int i = 0; i < threads.size(); i++) {
            threads[i].join();
        }
        cout << globalString;
        str = globalString;
    } /* while */
    
    return str;
}

int main() {
    ifstream in("grammar.in");
    int numberOfProductions;
    string str;
    
    unordered_map<char, vector<string> > m;

    in >> numberOfProductions;

    string nonterminal;
    string production;
    for (int i = 0; i < numberOfProductions; i++) {
        in >> nonterminal;
        if (nonterminal.size() != 1) {
            cout << "Please provide only one-letter nonterminal for the ";
            cout << i << " production.\n";
            return 0;
        }
        in >> production;
        m[nonterminal[0]].push_back(production);
    } 

    in >> str;

    string derivedWord = solve(m, str);
    cout << "\n\nFinal word: " << derivedWord;
    cout << "\n";

    return 0;
}
