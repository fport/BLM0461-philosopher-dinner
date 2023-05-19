import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Dusunur extends Thread {
    private final Lock solCatal;
    private final Lock sagCatal;
    private final int id;
    private int yemekSayisi = 0;

    public Dusunur(Lock solCatal, Lock sagCatal, int id) {
        this.solCatal = solCatal;
        this.sagCatal = sagCatal;
        this.id = id;
    }

    public void run() {
        while (true) {
            dusun();
            ye();
        }
    }

    public void dusun() {
        System.out.println(id + ". Düşünür düşünüyor.");
        try {
            Thread.sleep((int) (Math.random() * 10));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void ye() {
        if (solCatal.tryLock()) {
            if (sagCatal.tryLock()) {
                try {
                    yemekSayisi++;
                    System.out.println(id + ". Düşünür yemek yiyor. Yemek sayısı: " + yemekSayisi);
                    Thread.sleep((int) (Math.random() * 10));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    sagCatal.unlock();
                }
            }
            solCatal.unlock();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        final int dusunurSayisi = 5;
        Lock[] catalar = new ReentrantLock[dusunurSayisi];
        for (int i = 0; i < dusunurSayisi; i++) {
            catalar[i] = new ReentrantLock();
        }
        Dusunur[] dusunurler = new Dusunur[dusunurSayisi];
        for (int i = 0; i < dusunurSayisi; i++) {
            Lock solCatal = catalar[i];
            Lock sagCatal = catalar[(i + 1) % dusunurSayisi];
            if (i == dusunurSayisi - 1) {
                // Deadlock'u önlemek için bir düşünür önce sağ çatalı alacak şekilde tasarlanmıştır.
                dusunurler[i] = new Dusunur(sagCatal, solCatal, i+1);
            } else {
                dusunurler[i] = new Dusunur(solCatal, sagCatal, i+1);
            }
            dusunurler[i].start();
        }
    }
}
