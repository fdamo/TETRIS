import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Time;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class tetris02completo {
    public static void main(String[] args) {
        // creo oggetto JFrame
        JFrame frame = new JFrame("TETRIS");

        // imposta l'operazione di default di chiusura
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        PannelloTetrisCompleto pluto = new PannelloTetrisCompleto();
        JPanel pannello_pluto = new JPanel();
        
        JButton down = new JButton("giù");
        JButton dxt = new JButton("dxt");
        JButton sxt = new JButton("sxt");
        JButton rot = new JButton("ruota");

        rot.addActionListener(pluto);
        down.addActionListener(pluto);
        dxt.addActionListener(pluto);
        sxt.addActionListener(pluto);
        
        pannello_pluto.add(rot);
        pannello_pluto.add(down);
        pannello_pluto.add(sxt);
        pannello_pluto.add(dxt);

        frame.getContentPane().add(pannello_pluto, BorderLayout.NORTH);
        frame.getContentPane().add(pluto, BorderLayout.CENTER);


        // prepara la finestra . la fa vedere al minimo delle dimensioni
        frame.pack();

        // chiediamo di mostrare la finestra
        frame.setVisible(true);

    }

}


class PannelloTetrisCompleto extends JPanel implements ActionListener {
    
class Cella {
   
    int color = 0;
}

    public PannelloTetrisCompleto() {
         // inizializzare le variabili
        clearScreen();
        pezzo_corrente = pezzo_elle[0];

        disegnaPezzo(pezzo_corrente, x_corrente, y_corrente);

        timer = new Timer();
        anima = new AnimaGioco();
        timer.schedule(anima, 1000, 1000);
    }

    Timer timer;
    TimerTask anima;

    class AnimaGioco extends TimerTask {
        public void run() {
            moveDown();;
        }
    }

    int screen_w = 18;
    int screen_h = 33;
    
    int cellsize = 20;
    
    Cella [][] schermo = new Cella [screen_h][screen_w];


    int x_corrente = 0;
    int y_corrente = screen_w / 2-2;
    int[][] pezzo_corrente;
    
    int indice_pezzo_corrente = 0;
    int indice_rotazione_corrente = 0;
    
    int[][][] pezzo_elle = {{
            {0, 1, 0},
            {0, 1, 0},
            {0, 1, 1}
    }};

    int[][][] pezzo_esse = {{
        {0, 1, 0},
        {0, 1, 1},
        {0, 0, 1}
}};

    int[][][] pezzo_t = {{
        {0, 1, 0},
        {0, 1, 1},
        {0, 1, 0}
    }};

    int[][][][] array_pezzi = {
        pezzo_elle,
        pezzo_esse,
        pezzo_t
    }; 

    public void iniziaConNuovoPezzo() {
        estraiPezzo();
        x_corrente = 0;
        y_corrente = screen_w / 2 -2;
    }

    public void estraiPezzo() {
        
        double valore = Math.random();

        indice_rotazione_corrente = 0; 
        indice_pezzo_corrente = (int) valore * array_pezzi[0].length;
        pezzo_corrente = array_pezzi[indice_pezzo_corrente][indice_rotazione_corrente];

    }

    public void clearScreen(){
        for(int j=0; j<screen_h; j++) {
                for(int i=0; i<screen_w; i++) {
                    
                    Cella elem = new Cella();
                    schermo[j][i] = elem;

                    if(i < 2 || i > screen_w -3 || j >= screen_h -2) {
                        colorami(elem);
                    }
                    else
                        cancellami(elem);
                }
            }  
    }

    public void colorami(Cella elem) {
        elem.color = 1;
    }
    
    public void cancellami(Cella elem) {
        elem.color = 0;
    }

    public int leggiValoreCella(Cella elem) {
        if(elem.color == 0)
            return 0;
        else
            return 1;
    }

    public void disegnaPezzo(int[][] pezzo, int x, int y) {
        for(int i=0; i<3; i++) {
            for(int j=0; j<3; j++) {
                if( pezzo[i][j] == 1 )  {
                    int posx = x + i;
                    int posy = y + j;
                    Cella elem = schermo[posx][posy];
                    colorami(elem);
                }
            }
        }
        repaint();
    }

    public void cancellaPezzo(int[][] pezzo, int x, int y) {
        for(int i=0; i<3; i++) {
            for(int j=0; j<3; j++) {
                if( pezzo[i][j] == 1 )  {
                    int posx = x + i;
                    int posy = y + j;
                    Cella elem = schermo[posx][posy];
                    cancellami(elem);
                }
            }
        }
    }

    /*
    Restituisce la matrice del pezzo corrente ruotata
    */
    public int[][] ruotaPezzo() {
        int[][][] rotazioni = array_pezzi[indice_pezzo_corrente];
        indice_rotazione_corrente = indice_rotazione_corrente +1;
        if (indice_rotazione_corrente >= rotazioni.length)
            indice_rotazione_corrente = 0;
            
        //console.log("ruoto pezzo: ", pezzo_corrente, indice_rotazione_corrente);
        int[][] matrice = array_pezzi[indice_pezzo_corrente][indice_rotazione_corrente];
        //console.log("matrice: ", matrice);
        return matrice;
    }

    public void rotate() {
        cancellaPezzo(pezzo_corrente, x_corrente, y_corrente);
        int[][] pezzo_temp = ruotaPezzo();
        if(controllaCollisione(pezzo_temp, x_corrente, y_corrente) == true) {
            //console.log("collisioni in rotazione");
            disegnaPezzo(pezzo_corrente, x_corrente, y_corrente);
            return;
        }
        //console.log("disegna pezzo ruotato ");
        pezzo_corrente = pezzo_temp;
        disegnaPezzo(pezzo_corrente, x_corrente, y_corrente);
    }

    public void moveDown() {
        cancellaPezzo(pezzo_corrente, x_corrente, y_corrente);
        int x_temp = x_corrente +1;
        
        if(controllaCollisione(pezzo_corrente, x_temp, y_corrente) == true) {
            disegnaPezzo(pezzo_corrente, x_corrente, y_corrente);
            controllaRighePiene();      
            iniziaConNuovoPezzo();
            return;
        }       
        x_corrente = x_temp;
        disegnaPezzo(pezzo_corrente, x_corrente, y_corrente);
    }

    public void moveLeft() {
        cancellaPezzo(pezzo_corrente, x_corrente, y_corrente);
        int y_temp = y_corrente -1;

        if(controllaCollisione(pezzo_corrente, x_corrente, y_temp) == true) {
            disegnaPezzo(pezzo_corrente, x_corrente, y_corrente);
            return;
        }
      
        y_corrente = y_temp;
        disegnaPezzo(pezzo_corrente, x_corrente, y_corrente);
    }

    public void moveRight() {
        cancellaPezzo(pezzo_corrente, x_corrente, y_corrente);
        int y_temp = y_corrente +1;

        if(controllaCollisione(pezzo_corrente, x_corrente, y_temp) == true) {
            disegnaPezzo(pezzo_corrente, x_corrente, y_corrente);
            return;
        }
    
        y_corrente = y_temp;
        disegnaPezzo(pezzo_corrente, x_corrente, y_corrente);
    }


        /* 
    funzione che controlla se il pezzo collide con lo schermo o altri pezzi
    Ritorna: valore vero o falso (vero se c'è collisione)
    */    
    public boolean controllaCollisione(int[][] pezzo, int x, int y) {
        for(int i=0; i<3; i++) {
            for(int j=0; j<3; j++) {

                int valore_cella_pezzo = pezzo[i][j];
                int posx = x + i;
                int posy = y + j;
                
                Cella cella_schermo = schermo[posx][posy];
                int valore_cella_schermo = leggiValoreCella(cella_schermo);

                if(valore_cella_pezzo + valore_cella_schermo > 1) {
                    //abbiamo trovato una collisione
                    return true;
                }
            }
        }
        return false;
    }



    /*
    funzione che ritorna true se la riga di indice indice_riga 
    ha tutte le celle piene
    */
    public boolean controllaRigaPiena(int indice_riga) {
        if(indice_riga >= screen_h - 2)
            return false;

        for(int i=0; i<screen_w; i++) {
            
            Cella cella_schermo = schermo[indice_riga][i];
            int valore = leggiValoreCella(cella_schermo);
           
            if(valore == 0)
                return false;
        }
        return true;
    }

    public void controllaRighePiene() {

        for(int j=0; j<3; j++) {
            int i_riga = x_corrente + j;
            boolean valore = controllaRigaPiena(i_riga);

            if(valore == true) {
                cancellaRiga(i_riga);
            }
        }
    }

        /*
    cancella tutta la riga copiando i valori delle celle della riga superiore
    */
    public void cancellaRiga(int indice_riga) {
        
        for(int j=indice_riga; j>0; j--) {
            for(int i=0; i<screen_w; i++) {
                
                Cella  elem = schermo[j][i];
                Cella  elem_sopra = schermo[j -1][i];

                int valoreSopra = leggiValoreCella(elem_sopra);
                if(valoreSopra == 1)
                    colorami(elem);
                else
                    cancellami(elem);
            }
        }
    }  

    @Override
    protected void paintComponent(Graphics g) {
       super.paintComponent(g);

    for(int j=0; j<screen_h; j++) {

        int posy = j * cellsize;
        for(int i=0; i<screen_w; i++) {

            if(i < 2 || i > screen_w - 3 || j >= screen_h - 3) {

            }
            
            Cella elem = schermo[j][i];
            int color = elem.color;

            int posx = i * cellsize;

            g.setColor(Color.WHITE);
            if(color == 1)
                g.setColor(Color.orange);

            g.fillRect(posx, posy, cellsize, cellsize);

            g.setColor(Color.BLACK);
            g.drawRect(posx, posy, cellsize, cellsize);
            }
        }
    }
    public Dimension getPreferredSize() {

        return new Dimension(screen_w * cellsize, screen_h * cellsize);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      
        String cmd = e.getActionCommand();
        System.out.println("Bottone premuto"+ cmd );
        if(cmd.equals("dxt"))
            moveRight();
        else if(cmd.equals("sxt"))
            moveLeft();
        else if(cmd.equals("giu"))
            moveDown();
        else
            rotate();
    }
}
