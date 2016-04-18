import multiprocessing as mp
import itertools
import time
import csv
import urllib

count = 0
NUM_PROCESS = 2
OUTPUT_DIR = '/scratch/ajr619/review-rehashed/data/sitemaps.3/'

def worker(chunk):
    url = chunk[0][0]
    filename = url[url.rfind("/")+1:]
    urllib.urlretrieve (url,"{0}{1}".format(OUTPUT_DIR, filename)) 

def keyfunc(row):
    global count
    count = (count+1)%NUM_PROCESS
    return count

def main():
    #URLS_FILE = '/home/ajr619/workspace/amazon/all_sitemap.urls'
    URLS_FILE = '/home/ajr619/workspace/amazon/remaining.3.urls'

    pool = mp.Pool()

    iteration = 0
    with open(URLS_FILE,'r') as inf:
        reader = csv.reader(inf)
        chunks = itertools.groupby(reader, keyfunc)
        while True:
            # make a list of NUM_PROCESS chunks
            groups = [list(chunk) for key, chunk in itertools.islice(chunks, NUM_PROCESS)]
            if groups:
                result = pool.map(worker, groups)
            else:
                break
            iteration = iteration +1
            time.sleep(0.3)
            if(iteration % 50 == 0):
                time.sleep(5)
                print "Done downloading ",iteration*NUM_PROCESS

    pool.close()
    pool.join()

if __name__ == '__main__':
    main()
