import numpy as np
import matplotlib.pyplot as plt
import emcee
import corner
import math
from past.builtins import xrange
import csv


ice_data = np.loadtxt('ice_core_data.txt', delimiter=',', usecols=range(5))
ice_data = np.transpose(ice_data)
x = ice_data[2]
T = ice_data[4]
#plt.plot(age,T)
#plt.xlabel('age')
#plt.ylabel(r'$\Delta T$')
#plt.show()

def model(x):
    #x,a2,a3,p1,p2,p3,T0 = theta
    if x < 0:
        model = ((17*(math.exp(4)+math.exp(x-2)) + 1)/1000*(1+math.exp(4)+math.exp(x-2)))/100
    else:
        model = ((17*(math.exp(4)+math.exp(-x-2)) + 1)/1000*(1+math.exp(4)+math.exp(-x-2)))/100
    return model

def lnlike(theta, x, y, yerr):
    model2 = np.vectorize(model)
    return -0.5 * np.sum(((y - model2(x))/yerr) ** 2)

def lnprior(theta):
    a1, a2, a3, p1, p2, p3, T0 = theta
    #print (type(theta																))
    if 0.0 < a1 < 5.0 and 0.0 < a2 < 5.0 and 0.0 < a3 < 5.0 and 10000. < p1 < 200000 and 10000. < p2 < 200000 and 10000. < p3 < 200000 and -10.0 < T0 < 0: #apply conditions on priors here
        return 0.0
    else:
        return -np.inf

def log_prob(x, mu, cov):
    #p = lnprior(theta) #call lnprx`ior
    diff = x - mu
    return -0.5 * np.dot(diff, np.linalg.solve(cov, diff))


#nwalkers = 32
niter = 100
#initial = np.array([101.0, 121.0, 111.0, 160., 110.,100.,114.5])
#ndim = len(initial)
ndim = 5
mu=0
np.random.seed(42)
means = np.random.rand(ndim)

cov = 0.5 - np.random.rand(ndim ** 2).reshape((ndim, ndim))
cov = np.triu(cov)
cov += cov.T - np.diag(cov.diagonal())
cov = np.dot(cov, cov)

nwalkers = 32
#p0 = [np.array(initial) + 1e-7 * np.random.randn(ndim) for i in xrange(nwalkers)]
p0 =  np.random.rand(nwalkers, ndim)

def main(p0,nwalkers,niter,ndim,log_prob):
    sampler = emcee.EnsembleSampler(nwalkers, ndim, log_prob, args=[means, cov])
    log_prob(p0[0], means, cov)

    print("Running burn-in...")
    state = sampler.run_mcmc(p0, 100)
    sampler.reset()

    print("Running production...")
    pos, prob, state = sampler.run_mcmc(state, 10000);

    samples = sampler.get_chain(flat=True)
    print(samples[:, 0])
    plt.hist(samples[:, 0], 100, color="b", histtype="step")
    plt.xlabel(r"$\theta_1$")
    plt.ylabel(r"$p(\theta_1)$")
    plt.gca().set_yticks([]);
    #plt.xlim(100, 101)
    #plt.ylim(0, 0.03)
    #samples = sampler.flatchain
    #chain = sampler.chain
    #print(np.asarray(chain.flatten().tolist()))
    #plt.title("Matplotlib demo") 
    #plt.plot(np.asarray(chain.flatten().tolist())) 
    #plt.xlabel("x axis caption") 
    #plt.ylabel("y axis caption") 
    plt.show()
    print("Mean acceptance fraction: {0:.3f}".format(np.mean(sampler.acceptance_fraction)))
    print(
    "Mean autocorrelation time: {0:.3f} steps".format(
        np.mean(sampler.get_autocorr_time())
        )
    )
    file = open('mcmcdata.csv', 'w+', newline ='')
    with file:      
        write = csv.writer(file) 
        for x in samples[np.random.randint(len(samples), size=100)]:
            model2 = np.vectorize(model)
            mx = model2(x)
            #plt.plot(x, model2(x), color="r", alpha=0.1)
            for xind, xitem in enumerate(x):
                write.writerow([x[xind], mx[xind]])
    #plt.ticklabel_format(style='sci', axis='x', scilimits=(0,0))
    #plt.xlabel('Years ago')
    #plt.ylabel(r'$\Delta$ T (degrees)')
    #plt.legend()
    #plt.show()
    
    #sampler, pos, prob, state = main(p0,nwalkers,niter,ndim,lnprob,data)
    #plotter(sampler)
    return sampler, pos, prob, state

def plotter(sampler,x,T=T):
    plt.ion()
    plt.plot(x,T,label='Change in T')

new_sampler, newpos, newprob, newstate = main(p0,nwalkers,niter,ndim,log_prob)
