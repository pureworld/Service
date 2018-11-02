package org.pw.musicplayer_server;


interface IMyBinder
{
   void play();
   void pause();
   int getDuration();
   int getCurPos();
   boolean isPlaying();
   void setProgress(in int progress);
}