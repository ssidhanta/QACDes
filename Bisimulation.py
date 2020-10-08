import networkx as nx
import matplotlib.pyplot as plt
from networkx.drawing.nx_agraph import write_dot, graphviz_layout

file_name = '/home/subhajitsidhanta/Dropbox/Documents/GitHub/QORSensitiveDesign/VRDesignIOTSEdges'

# read .tra file (transition matrix) to determine transitions
with open(file_name+'.tra', 'rb') as tra:
    next(tra, '')   # skip the first line
    G = nx.read_edgelist(tra, data=[('probability', float)], create_using= nx.DiGraph())

# read .sta file to determine states    
lines = [line.rstrip('\n') for line in open(file_name + '.sta')]
del lines[0] # skip the first line
for li in lines:    
    if li != '':
        state_num = li.split(":")[0]
        state_data = li.split(":")[1].split(",")[0][1:] # state label (value of the variable l)
        G.add_node(state_num, data=state_data)
            
# state labels    
node_labels = nx.get_node_attributes(G, 'data') 

# Kanellakis Smolka algorithm is used to compute strong bisimulation equivalence classes.
# For more info on strong bisimulation, please see: Section 7.1 of "Principles of model checking", Christel Baier and Joost-Pieter Katoen
# For more info on Kanellakis Smolka algorithm, please see: Section 7.3 of "Principles of model checking", Christel Baier and Joost-Pieter Katoen


def initial_partition(s1, s2):
    """
    return true if labels of s1 and s2 are equal
    """
    return node_labels[s1] == node_labels[s2]

def pre(G, C):
    """
    return a list of predecessors of nodes of C.
    nodes in a self-loop are not considered as a predecessor.
    """
    pre = set() # empty set
    for s in C: # C is a set of nodes
        for u in G.predecessors(s):
            if u != s:
                pre.add(u)
            
    return pre

def refine(Pi, C):
    """
    Refine each block B of Pi into B&pre(C) and B-pre(C).
    Pi is a list of sets
    """
    pre_C = pre(G, C)
    for B in Pi:
        inter = B & pre_C
        diff = B - pre_C
        if len(inter) != 0 and len(diff) != 0:
            Pi.remove(B)
            Pi.append(inter)
            Pi.append(diff)
    return Pi
        
def Kanellakis_Smolka(G):
    Q = nx.quotient_graph(G, partition=initial_partition)
    Pi_AP = [n for n in Q]
    Pi = Pi_AP
    Pi_old = []
    while Pi_old != Pi:
        Pi_old = Pi.copy()
        for C in Pi_old:
            refine(Pi, C)
    return Pi

print(Kanellakis_Smolka(G))
