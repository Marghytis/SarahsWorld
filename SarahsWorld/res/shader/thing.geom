#version 150

uniform vec2 scale;
uniform vec2 offset;

layout (points) in;
layout (triangle_strip, max_vertices = 4) out;
 
in Vertex
{
  float pass_rotation;
  vec2 pass_texXY;
  float pass_mirror;
  vec4 pass_color;
  float pass_size;
  vec4 pass_box;//right, up, left, down
  vec2 pass_texWH;
} vertex[];
 
 
out vec2 texCoords;
out vec4 color;
   
void main (void)
{
 
  vec2 P = gl_in[0].gl_Position.xy + offset;
  float z = gl_in[0].gl_Position.z;
  
  vec4 box1 = vec4(vertex[0].pass_box.xy*vertex[0].pass_size, vertex[0].pass_box.zw*vertex[0].pass_size);
  float c = cos(vertex[0].pass_rotation);
  float s = sin(vertex[0].pass_rotation);
  mat2 rot = mat2(c, -s, s, c);
  vec2 p1 = rot*box1.xy;
  vec2 p2 = rot*box1.zy;
  vec2 p3 = rot*box1.xw;
  vec2 p4 = rot*box1.zw;
 
  //lower left
  gl_Position = vec4((P + p1)*scale, z, 1.0);
  texCoords = vec2(vertex[0].pass_texXY.x + vertex[0].pass_mirror, vertex[0].pass_texXY.y + vertex[0].pass_texWH.y);
  color = vertex[0].pass_color;
  EmitVertex();
 
  //lower right
  gl_Position = vec4((P + p2)*scale, z, 1.0);
  texCoords = vec2(vertex[0].pass_texXY.x + vertex[0].pass_texWH.x - vertex[0].pass_mirror, vertex[0].pass_texXY.y + vertex[0].pass_texWH.y);
  color = vertex[0].pass_color;
  EmitVertex();
 
  //upper left
  gl_Position = vec4((P + p3)*scale, z, 1.0);
  texCoords = vec2(vertex[0].pass_texXY.x + vertex[0].pass_mirror, vertex[0].pass_texXY.y);
  color = vertex[0].pass_color;
  EmitVertex();
 
  //upper right
  gl_Position = vec4((P + p4)*scale, z, 1.0);
  texCoords = vec2(vertex[0].pass_texXY.x + vertex[0].pass_texWH.x - vertex[0].pass_mirror, vertex[0].pass_texXY.y);
  color = vertex[0].pass_color;
  EmitVertex();
  
  EndPrimitive();  
}   