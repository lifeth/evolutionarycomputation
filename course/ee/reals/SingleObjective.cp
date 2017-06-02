#include <bits/stdc++.h>
#define _ ios_base::sync_with_stdio(false);cout.tie(0);cin.tie(0);
using namespace std;

class Region {
public:
  virtual double *gen(int n, double lo, double hi) = 0;
};

class Uniform : public Region {
public:
  Uniform() {}

  double *gen(int n, double lo, double hi) {
    double *x = new double[n];

    for (int i = 0; i < n; i++) {
      x[i] = lo+1.0*rand()/RAND_MAX*(hi-lo);
    }
    return x;
  }
};

class Normal : public Region {
public:
  Normal(double sigma = 1) {
    this->sigma = sigma;
    dist = normal_distribution<double>(0, sigma);
  }
  double *gen(int n, double lo, double hi) {
    double *x = new double[n];

    for (int i = 0; i < n; i++) {
      x[i] = dist(generator);
    }
    return x;
  }
  double gen() {
    return dist(generator);
  }
  
  default_random_engine generator;
  normal_distribution<double> dist;
  double sigma;
};

class Stair {
public:
  Stair(double dist[], int n) {
    this->dist = dist;
    this->n = n;
  }
  int gen(double u) {
    double acc = 0;

    for (int i = 0; i < n; i++) {
      acc += dist[i];

      if (acc > u) {
        return i;
      }
    }
    return -1;
  }
  int n;
  double *dist;
};

class Function {
public:
  Function(int n) {
    this->n = n;
  }
  virtual double eval(double x[]) = 0;
  int n;
};

class Rastrigin : public Function {
public:
  Rastrigin(int n, int AA) : Function(n) {
    A = AA;
  }
  double eval(double x[]) {
    double y = A*n;

    for (int i = 0; i < n; i++) {
      y += x[i]*x[i]-A*cos(2*M_PI*x[i]);
    }
    return y;
  }
  int A;
};

class Optimizer {
public:
  virtual double *eval(Function *f) = 0;
};

int run = 0;
const int R = 30, I = 1000000;
double fitI[I][R], maxI[I], minI[I], avgI[I], medI[I], stdI[I];

void init() {
  
  for (int i = 0; i < I; i++) {
    for (int j = 0; j < R; j++) {
      fitI[i][j] = 10000;
    }
    medI[i] = stdI[i] = 0;
    maxI[i] = -100000;
    minI[i] = 100000;
  }
}

void getStd() {
  
  vector<double> arr;

  for (int i = 0; i < I; i++) {
    arr.clear();
    
    for (int j = 0; j < R; j++) {
      arr.push_back(fitI[i][j]);
    }
    sort(arr.begin(), arr.end());
    
    int mi = R/2;
    double med = (arr[mi]+arr[mi-1])/2.;

    for (int j = 0; j < R; j++) {
      stdI[i] += (fitI[i][j]-med)*(fitI[i][j]-med);
    }
    stdI[i] = sqrt(stdI[i]/R);
    medI[i] = med;
  }
}

class HillClimb : public Optimizer {
public:
  HillClimb(int iters, double lo, double hi, Region *reg) {
    this->iters = iters;
    this->lo = lo;
    this->hi = hi;
    this->reg = reg;
  }
  double *eval(Function *f) {
    Uniform uni;
    int n = f->n;
    double *x = uni.gen(n, lo, hi);
    double *y = new double[n];
    double r = f->eval(x);

    for (int i = 0; i < iters; i++) {
      double *d = reg->gen(n, -1, 1);

      for (int j = 0; j < n; j++) {
        y[j] = x[j]+d[j];
        y[j] = min(hi, max(y[j], lo));
      }
      double nr = f->eval(y);
      if (nr < r) {
        memcpy(x, y, n*sizeof(double));
        r = nr;
      }
      nr = f->eval(x);
      fitI[i][run] = min(fitI[i][run], nr);
      maxI[i] = max(maxI[i], nr);
      minI[i] = min(minI[i], nr);
      medI[i] += nr;
      avgI[i] += nr;
      delete [] d;
    }
    delete [] y;
    return x;
  }
  int iters;
  double lo, hi;
  Region *reg;
};

class SwarmParticle : public Optimizer {
public:
  SwarmParticle(int iters, double lo, double hi, Region *reg) {
    this->iters = iters;
    this->lo = lo;
    this->hi = hi;
    this->reg = reg;
  }
  double *eval(Function *f) {

    const int POP = 100;
    double delta = 0.1;
    int n = f->n;
    double *v = new double[n];
    double fit = 1e7;
    int best = -1;
    double **pop = new double*[POP];

    for (int i = 0; i < POP; i++) {
      pop[i] = reg->gen(n, lo, hi);
    }
    for (int i = 0; i < iters+1; i++) {
      for (int j = 0; j < POP; j++) {
        double fj = f->eval(pop[j]);

        fitI[i][run] = min(fitI[i][run], fj);
        maxI[i] = max(maxI[i], fj);
        minI[i] = min(minI[i], fj);
        medI[i] += fj;
        avgI[i] += fj;

        if (fj < fit) {
          fit = fj;
          best = j;
        }
      }
      for (int k = 0; k < POP; k++) {
        if (k == best) {
          continue;
        }
        double norm = 0;

        for (int j = 0; j < n; j++) {
          v[j] = pop[best][j]-pop[k][j];
          norm += v[j]*v[j];
        }
        norm = sqrt(norm);

        for (int j = 0; j < n; j++) {
          pop[k][j] += delta*(v[j]/norm);
        }
      }
    }
    memcpy(v, pop[best], n*sizeof(double));

    for (int i = 0; i < POP; i++) {
      delete [] pop[i];
    }
    delete [] pop;
    return v;
  }
  int iters;
  double lo, hi;
  Region *reg;
};

class SolutionES {
public:
  SolutionES() {
    sol = std = NULL;
    fit = 1e9;
  }
  
  SolutionES(double *sol, double *std, double fit) {
    this->sol = sol;
    this->std = std;
    this->fit = fit;
  }

  void copy(SolutionES *ses, int n) {
  
    memcpy(sol, ses->sol, n*sizeof(double));
    memcpy(std, ses->std, n*sizeof(double));
    fit = ses->fit;
  }

  ~SolutionES() {

    delete [] sol;
    delete [] std;
  }
  
  bool operator<(const SolutionES &ses) const {
    return fit < ses.fit;
  }
  
  double *sol;
  double *std;
  double fit;
};

class EvolutionaryStrategy : public Optimizer {
public:
  EvolutionaryStrategy(int iters, double lo, double hi, int u, int a, Region *reg) {
    this->iters = iters;
    this->U = u;
    this->A = a;
    this->reg = reg;
    this->lo = lo;
    this->hi = hi;
  }

  void mutate_sol(double *sol, double *std, int n) {
    
    for (int i = 0; i < n; i++) {
      sol[i] += std[i]*norm.gen();
      if (sol[i] < lo) sol[i] = lo;
      if (sol[i] > hi) sol[i] = hi;
    }
  }

  void mutate_std(double *std, int n) {

    double t  = 1./sqrt(2.*n);
    double tp = 1./sqrt(2.*sqrt(n));
    double P = norm.gen();

    for (int i = 0; i < n; i++) {
      std[i] *= exp(t*P+tp*norm.gen());
    }
  }

  double *eval(Function *f) {
  
    int n = f->n;
    SolutionES **pop = new SolutionES*[U];
    SolutionES **tmp = new SolutionES*[U];    
    SolutionES **next = new SolutionES*[A];
    
    for (int i = 0; i < U; i++) {
      double *sol = reg->gen(n, lo, hi);
      double *std = norm.gen(n, lo, hi);
      pop[i] = new SolutionES(sol, std, 0);
      pop[i]->fit = f->eval(pop[i]->sol);
      tmp[i] = new SolutionES(new double[n], new double[n], 0);
    }
    for (int i = 0; i < A; i++) {
      double *sol = reg->gen(n, lo, hi);
      double *std = norm.gen(n, lo, hi);
      next[i] = new SolutionES(sol, std, 1e9);
    }

    for (int i = 0; i < iters; i++) {
      for (int j = 0; j < U; j++) {
        tmp[j]->copy(pop[j], n);
      }
      for (int j = 0; j < A; j++) {
        next[j]->copy(pop[j%U], n);
        mutate_sol(next[j]->sol, next[j]->std, n);
        mutate_std(next[j]->std, n);
        next[j]->fit = f->eval(next[j]->sol);
      }
      sort(next, next+A);
      sort(tmp, tmp+U);
      
      for (int j = 0, a = 0, p = 0; j < U; j++) {
        if (next[a]->fit < tmp[p]->fit) {
          pop[j]->copy(next[a], n);
          a++;
        }
        else if (next[a]->fit > tmp[p]->fit) {
          pop[j]->copy(tmp[p], n);
          p++;
        } else {
          pop[j]->copy(tmp[p], n);
          a++;
          p++;
        }
      }
      for (int j = 0; j < U; j++) {
        double nr = pop[j]->fit;
        fitI[i][run] = min(fitI[i][run], nr);
        maxI[i] = max(maxI[i], nr);
        minI[i] = min(minI[i], nr);
        medI[i] += nr;
        avgI[i] += nr;
      }
    }
    double *x = new double[n];
    memcpy(x, pop[0]->sol, n*sizeof(double));

    for (int i = 0; i < U; i++) {
      delete pop[i];
      delete tmp[i];
    }
    for (int i = 0; i < A; i++) {
      delete next[i];
    }
    delete [] pop;
    delete [] next;
    delete [] tmp;
    return x;
  }
  
  Region *reg;
  Normal norm;
  double lo, hi;
  int U, A;
  int iters;
};

class DifferentialEvolution : public Optimizer {
public:
  DifferentialEvolution(int iters, double lo, double hi, double F, double CR, Region *reg) {
  
    this->iters = iters;
    this->lo = lo;
    this->hi = hi;
    this->F = F;
    this->CR = CR;
    this->reg = reg;
  }
  double *eval(Function *f) {
  
    int POP = 100;
    int n = f->n;
    double **pop = new double*[POP];
    double *y = new double[n];
    
    for (int i = 0; i < POP; i++) {
      pop[i] = reg->gen(n, lo, hi);
    }
    for (int i = 0; i < iters; i++) {
      for (int j = 0; j < POP; j++) {
        int a, b, c, r = rand()%n;
        do {
          a = rand()%POP;
        } while (j == a);
        do {
          b = rand()%POP;
        } while (j == b || a == b);
        do {
          c = rand()%POP;
        } while (j == c || a == c || b == c);
        
        for (int d = 0; d < n; d++) {
          int r = 1.*rand()/RAND_MAX;
          
          if (r < CR || d == r) {
            y[d] = pop[a][d]+F*(pop[b][d]-pop[c][d]);
          } else {
            y[d] = pop[j][d];
          }
        }
        if (f->eval(y) < f->eval(pop[j])) {
          memcpy(pop[j], y, n*sizeof(double));
        }
      }
      for (int j = 0; j < POP; j++) {
        double nr = f->eval(pop[j]);
        fitI[i][run] = min(fitI[i][run], nr);
        maxI[i] = max(maxI[i], nr);
        minI[i] = min(minI[i], nr);
        medI[i] += nr;
        avgI[i] += nr;
      }
    }
    double bestF = 1e9;

    for (int i = 0; i < POP; i++) {
      double fit = f->eval(pop[i]);
      if (fit < bestF) {
        bestF = fit;
        memcpy(y, pop[i], n*sizeof(double));
      }
      delete [] pop[i];
    }
    delete [] pop;
    return y;
  }

  int iters;
  double lo;
  double hi;
  double F;
  double CR;
  Region *reg;
};

int main() { _
  srand(time(0));
  init();
  
  Uniform uni;
  Normal norm(0.2);
  Rastrigin ras(10, 10);
  HillClimb hc(I, -5.12, 5.12, &uni);
  SwarmParticle spo(I, -5.12, 5.12, &uni);
  EvolutionaryStrategy es(I, -5.12, 5.12, 100, 100, &uni);
  DifferentialEvolution de(I, -5.12, 5.12, 1, 0.5, &uni);

  for (; run < R; run++) {
    //double *x = spo.eval(&ras);
    //double *x = hc.eval(&ras);
    //double *x = es.eval(&ras);
    double *x = de.eval(&ras);
    delete [] x;
  }
  getStd();
  
  for (int i = 0; i < I; i++) {
    if (i%1000 == 0) {
      cout << i << "\t" << minI[i] << "\t" << maxI[i] << "\t" << medI[i] << "\t" << stdI[i] << endl;
    }
  }
  //for (int i = 0; i < ras.n; i++) {
  //  cout << x[i] << " ";
  //}
  //cout << "\n" << ras.eval(x) << "\n";

  //delete [] x;
  return 0;
}

