/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: D:\\workSpace\\VideoTV_huaWei\\VideoTV\\src\\com\\huawei\\iptv\\stb\\asr\\aidl\\neteast\\Callback.aidl
 */
package com.huawei.iptv.stb.asr.aidl.neteast;
public interface Callback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.huawei.iptv.stb.asr.aidl.neteast.Callback
{
private static final java.lang.String DESCRIPTOR = "com.huawei.iptv.stb.asr.aidl.neteast.Callback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.huawei.iptv.stb.asr.aidl.neteast.Callback interface,
 * generating a proxy if needed.
 */
public static com.huawei.iptv.stb.asr.aidl.neteast.Callback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.huawei.iptv.stb.asr.aidl.neteast.Callback))) {
return ((com.huawei.iptv.stb.asr.aidl.neteast.Callback)iin);
}
return new com.huawei.iptv.stb.asr.aidl.neteast.Callback.Stub.Proxy(obj);
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
case TRANSACTION_onResult:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.onResult(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.huawei.iptv.stb.asr.aidl.neteast.Callback
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
@Override public void onResult(java.lang.String json) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(json);
mRemote.transact(Stub.TRANSACTION_onResult, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_onResult = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void onResult(java.lang.String json) throws android.os.RemoteException;
}
