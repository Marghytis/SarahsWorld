#version 150

uniform vec2 scale;
uniform vec2 offset;
uniform vec4 box;//right, up, left, down
uniform vec2 texWH;

layout (points) in;
layout (triangle_strip, max_vertices = 4) out;
 
in Vertex
{
  float pass_rotation;
  vec2 texXY;
  float pass_mirror;
  vec4 pass_color;
  float pass_size;
} vertex[];
 
 
out vec2 texCoords;
out vec4 color;
   
void main (void)
{
 
  vec2 P = gl_in[0].gl_Position.xy + offset;
  float z = gl_in[0].gl_Position.z;
  
  vec4 box1 = vec4(box.xy*vertex[0].pass_size, box.zw*vertex[0].pass_size);
 
  //lower left
  gl_Position = vec4((P + box1.xy)*scale, z, 1.0);
  texCoords = vec2(vertex[0].texXY.x + vertex[0].pass_mirror, vertex[0].texXY.y + texWH.y);
  color = vertex[0].pass_color;
  EmitVertex();
 
  //lower right
  gl_Position = vec4((P + box1.zy)*scale, z, 1.0);
  texCoords = vec2(vertex[0].texXY.x + texWH.x - vertex[0].pass_mirror, vertex[0].texXY.y + texWH.y);
  color = vertex[0].pass_color;
  EmitVertex();
 
  //upper left
  gl_Position = vec4((P + box1.xw)*scale, z, 1.0);
  texCoords = vec2(vertex[0].texXY.x + vertex[0].pass_mirror, vertex[0].texXY.y);
  color = vertex[0].pass_color;
  EmitVertex();
 
  //upper right
  gl_Position = vec4((P + box1.zw)*scale, z, 1.0);
  texCoords = vec2(vertex[0].texXY.x + texWH.x - vertex[0].pass_mirror, vertex[0].texXY.y);
  color = vertex[0].pass_color;
  EmitVertex();
  
  EndPrimitive();  
}   