"""
Simple python programme to do monte carlo simulation, calculate var and plot histogram given simple python equation.
 Usage:
 python mc_simulation_helper.py < sample_input.txt


Sample Input File:

a + 10
10000
abcd.csv
25
100.0,0.0,U
0.84

Please note
First line  : "a + 10" is the model equation for our Monte Carlo Simulation. This could be "a + b + c" also, script can identify which
are the variables and ask for their distribution. 
Second line : 10000 number of simulations
Third line : Name of the output file.
Fourth line : Number of bins in histogram
Fifth line : Distrubution of only variable a . It is a uniform distrubution so parameter maximum and minimum values
are given. Summary of different distrubutions supported and how to specify those are as follows.
Normal Distrubution           : Mean, Variance, N example 0,1,N
Log Normal Distrubution       : Mean, Variance, LN example 0,1,LN
Uniform Distrubution          : Maximum, Minimum, U example 100,1,U
"""

from tokenize import generate_tokens, untokenize
from StringIO import StringIO
import token

def decistmt(s):

    """

    Please See :
    This is the function I copied from the python doc site. It is their work.
    But I understand how it works :) .

    """

    result = []

    g = generate_tokens(StringIO(s).readline)   # tokenize the string

    for toknum, tokval, _, _, _  in g:
        if toknum == token.NUMBER and '.' in tokval:  # replace NUMBER tokens
            result.extend([
                (token.NAME, 'Decimal'),
                (token.OP, '('),
                (token.STRING, repr(tokval)),
                (token.OP, ')')
            ])
        else:
            result.append((toknum, tokval))
    return result

def get_tokens(s):
    result = decistmt(s)
    string_result = []

    for (tok_number, tok_value) in result:
        t = (str(token.tok_name[tok_number]), str(tok_value))
        string_result.append(t)
    return string_result

def get_var_set(all_tokens):
    var_set = set()
    for (tok_type, tok_value) in all_tokens:
        if (tok_type=='NAME' and tok_value!='math' and tok_value!='exp'  and tok_value!='-' and  tok_value!='*'  and tok_value!='.' and  tok_value!='/' and  tok_value!='('  and  tok_value!=')'):
              var_set.add(tok_value)
    return var_set


def get_values_from_user(var_list):
    var_value_map = {}
    print "Please enter the mue, sigma and probabily distrubution for variables as comma separated values"
    print "Use LN for logarathmic normal, N for normal and U for uniform distrubution"
    print "Example : 19.0,5.0,LN"
    mue = None
    sigma = None
    prob_dist = None
    for eq_var in var_list:
        print "Please enter the mue, sigma and probabil (maximum, minimum and 'U' in case of normal distrubution) for %s" % (eq_var,)
        s=sys.stdin.readline()
        split_values = s.split('\n')[0].split(',')
        mue = split_values[0]
        sigma = split_values[1]
        prob_dist = split_values[2]
        var_value_map[eq_var] = {}
        var_value_map[eq_var]['mue'] = float(mue)
        var_value_map[eq_var]['sigma'] = float(sigma)
        var_value_map[eq_var]['prob_dist'] = prob_dist
    return var_value_map

print "Please enter your equation"
import sys

user_equation=sys.stdin.readline()
print "Please enter number of simulations"
number_of_simulations = int(sys.stdin.readline())
print "Please enter name of the output file"
output_file_name = sys.stdin.readline()[:-1]
print "how many different bins you want in histogram"
no_of_bins = int(sys.stdin.readline())
all_tokens = get_tokens(user_equation)
var_list = get_var_set(all_tokens)
print var_list
var_list = list(var_list)
var_values_from_user = get_values_from_user(var_list)
print "This is the var values we got from user"
print var_values_from_user
import random
import math
import time
NUMBER_OF_SIMULATIONS = number_of_simulations

def lognormal_mean(mu, sigma):
    sigma_s = sigma * sigma
    mu_s = mu * mu
    return math.log(mu) - 0.5 * math.log(float(sigma_s)/float(mu_s) + 1.0)

def lognormal_sigma(mu, sigma):
    sigma_s = sigma * sigma
    mu_s = mu * mu
    return math.sqrt(math.log(float(sigma_s)/float(mu_s) + 1.0))

def lognormal_max(mu, sigma):
    mean = lognormal_mean(mu, sigma)
    sigma = lognormal_sigma(mu, sigma)
    return math.e**(mean + 1.96 * sigma)

def lognormal_min(mu, sigma):
    mean = lognormal_mean(mu, sigma)
    sigma = lognormal_sigma(mu, sigma)
    return math.e**(mean - 1.96 * sigma)

parameters_ranges = {}

for (param, par_prop) in var_values_from_user.iteritems():
    parameters_ranges[param] = None;
    mue = par_prop['mue']
    sigma = par_prop['sigma']
    prob_dist = par_prop['prob_dist']
    if prob_dist is not 'U':
        max_val = mue + 20.0 * sigma
        min_val = mue - 20.0 * sigma
    else:
        max_val = mue
        min_val = sigma

    if prob_dist == 'LN':
        new_mue = lognormal_mean(mue, sigma)
        new_sigma = lognormal_sigma(mue, sigma)
        min_val = lognormal_min(mue, sigma)
        max_val = lognormal_max(mue, sigma)
        mue = new_mue
        sigma = new_sigma

    parameters_ranges[param] = (mue, sigma, prob_dist, min_val, max_val)

parameters_random_values = {}
for parameter, ranges in parameters_ranges.iteritems():
    par1 = ranges[0]
    par2 = ranges[1]
    prob_dist = ranges[2]
    minimum = ranges[3]
    maximum = ranges[4]
    ranges_array = []
    loop_counter = 0
    random.seed(time.time())
    parameters_random_values[parameter] = []
    while (loop_counter < NUMBER_OF_SIMULATIONS):
        if prob_dist == 'U':
            random_number = random.uniform(par1, par2)
        if prob_dist == 'N':
            random_number = random.normalvariate(par1, par2)
        if prob_dist == 'LN':
            random_number = random.lognormvariate(par1, par2)
        if minimum > random_number or maximum < random_number:
            continue
        parameters_random_values[parameter].append(random_number) 
        loop_counter = loop_counter + 1

loop_counter = 0

print "writing to file  " + str(output_file_name)
output_file = open(str(output_file_name), 'w')
output_file.write(",".join(var_list) + ",RESULT\n" )

all_results = [];
while (loop_counter < NUMBER_OF_SIMULATIONS):
    var_values = []
    for var_name in var_list:
         eval_str = var_name + " = " +  str(parameters_random_values[var_name][loop_counter])
         exec(eval_str)
         var_values.append(str(eval(var_name)))

    result = (eval(user_equation))
    var_values.append(str(result))
    all_results.append(result)
    output_file.write(",".join(var_values) + "\n")
    loop_counter = loop_counter + 1

output_file.close()

x=all_results;
## Please see
## Kind Attention
## Please remove this part of the code if you do not have pylabs installed so that other part of the code could work..
###
###
###

import numpy as np
import pylab as P
max_res = float(max(all_results))
min_res = float(min(all_results))
bins = range(int(min_res), int(max_res), (int(max_res) - int(min_res)) / int(no_of_bins))
# the histogram of the data with histtype='step'
n, bins, patches = P.hist(x, bins, normed=1, histtype='bar', rwidth=0.8)
P.figure()
P.show()

############### Remove till here ##################################

print "Please enter confidence level for var"
conf_lev = float(sys.stdin.readline())
var_index = len(all_results) - (int(conf_lev * len(all_results)))
conf_var = sorted(all_results)[var_index]
print " approx var for %s is %s" % (str(conf_lev), str(conf_var))
exit()
