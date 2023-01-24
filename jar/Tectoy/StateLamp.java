package com.tectoy.StateLamp;

public interface IStateLamp extends android.os.IInterface
{
  /** Default implementation for IStateLamp. */
  public static class Default implements com.tectoy.StateLamp
  {

    /**
         * Função: Controle uma única luz de alarme
         * Parâmetros:
         * [in]status       0 ligado, 1 desligado
         * [in]lamp         LED, parâmetros:
         *                      "Led-1"
         *                      "Led-2"
         *                      "Led-3"
         *                      "Led-4"
         *                      "Led-5"
         *                      "Led-6"
         * Valor de retorno: Nenhum
         */

    @Override public void controlLamp(int status, java.lang.String lamp) throws android.os.RemoteException
    {
    }

    /**
		* Função         : controle um único display de ciclo de luz de alarme
          * Parâmetros:
          * [in]status     : status, 0 inicia o loop, 1 interrompe o loop
          * [in]lightTime  : O tempo de ativação da luz do alarme, unidade: milissegundo (ms)
          * [in]putoutTime : Tempo de desligamento da luz do alarme, unidade: milissegundos (ms)
		* [in]lamp       : Led da lâmpada, parâmetros:
		*                      "Led-1"
		*                      "Led-2"
		*                      "Led-3"
		*                      "Led-4"
		*                      "Led-5"
		*                      "Led-6"          * Valor de retorno: Nenhum
    */
    @Override public void controlLampForLoop(int status, long lightTime, long putoutTime, java.lang.String lamp) throws android.os.RemoteException
    {
    }


    /**
         * Função: desligar todas as luzes de advertência
    */
    @Override public void closeAllLamp() throws android.os.RemoteException
    {
    }


    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }


  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements com.tectoy.StateLamp
  {
    private static final java.lang.String DESCRIPTOR = "com.tectoy.StateLamp";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.tectoy.StateLamp interface,
     * generating a proxy if needed.
     */
    public static com.tectoy.StateLamp asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof com.tectoy.StateLamp))) {
        return ((com.tectoy.StateLamp)iin);
      }
      return new com.tectoy.StateLamp.Stub.Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      java.lang.String descriptor = DESCRIPTOR;
      switch (code)
      {
        case INTERFACE_TRANSACTION:
        {
          reply.writeString(descriptor);
          return true;
        }
        case TRANSACTION_controlLamp:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          java.lang.String _arg1;
          _arg1 = data.readString();
          this.controlLamp(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_controlLampForLoop:
        {
          data.enforceInterface(descriptor);
          int _arg0;
          _arg0 = data.readInt();
          long _arg1;
          _arg1 = data.readLong();
          long _arg2;
          _arg2 = data.readLong();
          java.lang.String _arg3;
          _arg3 = data.readString();
          this.controlLampForLoop(_arg0, _arg1, _arg2, _arg3);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_closeAllLamp:
        {
          data.enforceInterface(descriptor);
          this.closeAllLamp();
          reply.writeNoException();
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements com.tectoy.StateLamp
    {
      private android.os.IBinder mRemote;
      Proxy(android.os.IBinder remote)
      {
        mRemote = remote;
      }
      @Override public android.os.IBinder asBinder()
      {
        return mRemote;
      }
      public java.lang.String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      @Override public void controlLamp(int status, java.lang.String lamp) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(status);
          _data.writeString(lamp);
          boolean _status = mRemote.transact(Stub.TRANSACTION_controlLamp, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().controlLamp(status, lamp);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void controlLampForLoop(int status, long lightTime, long putoutTime, java.lang.String lamp) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeInt(status);
          _data.writeLong(lightTime);
          _data.writeLong(putoutTime);
          _data.writeString(lamp);
          boolean _status = mRemote.transact(Stub.TRANSACTION_controlLampForLoop, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().controlLampForLoop(status, lightTime, putoutTime, lamp);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      @Override public void closeAllLamp() throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          boolean _status = mRemote.transact(Stub.TRANSACTION_closeAllLamp, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().closeAllLamp();
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      public static com.tectoy.StateLamp sDefaultImpl;
    }
    static final int TRANSACTION_controlLamp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_controlLampForLoop = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_closeAllLamp = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    public static boolean setDefaultImpl(com.tectoy.StateLamp impl) {
      // Only one user of this interface can use this function
      // at a time. This is a heuristic to detect if two different
      // users in the same process use this function.
      if (Stub.Proxy.sDefaultImpl != null) {
        throw new IllegalStateException("setDefaultImpl() called twice");
      }
      if (impl != null) {
        Stub.Proxy.sDefaultImpl = impl;
        return true;
      }
      return false;
    }
    public static com.tectoy.StateLamp getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  public void controlLamp(int status, java.lang.String lamp) throws android.os.RemoteException;
  public void controlLampForLoop(int status, long lightTime, long putoutTime, java.lang.String lamp) throws android.os.RemoteException;
  public void closeAllLamp() throws android.os.RemoteException;
}
