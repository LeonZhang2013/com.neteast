/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\workSpace\\VideoTV_huaWei\\VideoTV\\src\\com\\huawei\\iptv\\stb\\asr\\aidl\\neteast\\IAsrNeteastService.aidl
 */
package com.huawei.iptv.stb.asr.aidl.neteast;
public interface IAsrNeteastService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.huawei.iptv.stb.asr.aidl.neteast.IAsrNeteastService
{
private static final java.lang.String DESCRIPTOR = "com.huawei.iptv.stb.asr.aidl.neteast.IAsrNeteastService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.huawei.iptv.stb.asr.aidl.neteast.IAsrNeteastService interface,
 * generating a proxy if needed.
 */
public static com.huawei.iptv.stb.asr.aidl.neteast.IAsrNeteastService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.huawei.iptv.stb.asr.aidl.neteast.IAsrNeteastService))) {
return ((com.huawei.iptv.stb.asr.aidl.neteast.IAsrNeteastService)iin);
}
return new com.huawei.iptv.stb.asr.aidl.neteast.IAsrNeteastService.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_setCallback:
{
data.enforceInterface(DESCRIPTOR);
com.huawei.iptv.stb.asr.aidl.neteast.Callback _arg0;
_arg0 = com.huawei.iptv.stb.asr.aidl.neteast.Callback.Stub.asInterface(data.readStrongBinder());
this.setCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_execute:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _result = this.execute(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.huawei.iptv.stb.asr.aidl.neteast.IAsrNeteastService
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
@Override public void setCallback(com.huawei.iptv.stb.asr.aidl.neteast.Callback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_setCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public java.lang.String execute(java.lang.String json) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(json);
mRemote.transact(Stub.TRANSACTION_execute, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_setCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_execute = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public void setCallback(com.huawei.iptv.stb.asr.aidl.neteast.Callback callback) throws android.os.RemoteException;
public java.lang.String execute(java.lang.String json) throws android.os.RemoteException;
}
