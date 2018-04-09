package com.bn.Sample9_2;//声明包
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.content.res.Resources;
import android.opengl.GLES30;

//加载后的物体——携带顶点信息，自动计算面法向量
public class LoadedObjectVertexNormal
{	
	int mProgram;//自定义渲染管线着色器程序id  
    int muMVPMatrixHandle;//总变换矩阵引用
    int muMMatrixHandle;//位置、旋转变换矩阵
    int maPositionHandle; //顶点位置属性引用  
    int maNormalHandle; //顶点法向量属性引用  
    int maLightLocationHandle;//光源位置属性引用  
    int maCameraHandle; //摄像机位置属性引用 
    String mVertexShader;//顶点着色器代码脚本    	 
    String mFragmentShader;//片元着色器代码脚本    
	
	FloatBuffer   mVertexBuffer;//顶点坐标数据缓冲  
	FloatBuffer   mNormalBuffer;//顶点法向量数据缓冲
    int vCount=0;  //顶点数量
    
    public LoadedObjectVertexNormal(Resources r, float[] vertices, float[] normals)
    {    	
    	//初始化顶点坐标与法向量数据
    	initVertexData(vertices,normals);
    	//初始化着色器     
    	initShader(r);
    }
    
	//初始化顶点数据和法向量的方法
    public void initVertexData(float[] vertices,float[] normals)
    {
    	//顶点坐标数据的初始化================begin============================
    	vCount=vertices.length/3;   
		
        //创建顶点坐标数据缓冲
        //vertices.length*4是因为一个整数四个字节
        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());//设置字节顺序
        mVertexBuffer = vbb.asFloatBuffer();//转换为Float型缓冲
        mVertexBuffer.put(vertices);//向缓冲区中放入顶点坐标数据
        mVertexBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点坐标数据的初始化================end============================
        
        //顶点法向量数据的初始化================begin============================  
        ByteBuffer cbb = ByteBuffer.allocateDirect(normals.length*4);//创建顶点法向量数据缓冲
        cbb.order(ByteOrder.nativeOrder()); //设置字节顺序
        mNormalBuffer = cbb.asFloatBuffer();//转换为Float型缓冲
        mNormalBuffer.put(normals);//向缓冲区中放入顶点法向量数据
        mNormalBuffer.position(0);//设置缓冲区起始位置
        //特别提示：由于不同平台字节顺序不同数据单元不是字节的一定要经过ByteBuffer
        //转换，关键是要通过ByteOrder设置nativeOrder()，否则有可能会出问题
        //顶点着色数据的初始化================end============================
    }

    //初始化shader
    public void initShader(Resources r)
    {

        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex.sh", r );
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag.sh", r );
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);


        maPositionHandle = GLES30.glGetAttribLocation(mProgram, "aPosition");
        maNormalHandle= GLES30.glGetAttribLocation(mProgram, "aNormal");
        muMVPMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix");  


        muMMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMMatrix");
        maLightLocationHandle=GLES30.glGetUniformLocation(mProgram, "uLightLocation");
        maCameraHandle=GLES30.glGetUniformLocation(mProgram, "uCamera"); 
    }
    
    public void drawSelf()
    {
    	 GLES30.glUseProgram(mProgram);//制定使用某套着色器程序
         GLES30.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);//将最终变换矩阵传入着色器程序
         GLES30.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0); //将位置、旋转变换矩阵传入着色器程序
         GLES30.glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);//将光源位置传入着色器程序
         GLES30.glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);//将摄像机位置传入着色器程序

         GLES30.glVertexAttribPointer(maPositionHandle, 3, GLES30.GL_FLOAT, false, 3*4, mVertexBuffer);
         GLES30.glVertexAttribPointer(maNormalHandle, 3, GLES30.GL_FLOAT, false, 3*4, mNormalBuffer);
         GLES30.glEnableVertexAttribArray(maPositionHandle);  
         GLES30.glEnableVertexAttribArray(maNormalHandle);  

         GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, vCount); 
    }
}
