using UnityEngine;
using System.Collections;

public class WavesPerlinNoise : MonoBehaviour {

	public float scale = 1.0f;
	public float speed = 1.0f;
	public float noiseStrength = 1.0f;
	public float noiseWalk = 1.0f;
	
	private Vector3[] baseHeight;
	private MeshCollider meshCollider;
	private Mesh mesh;
	
	void Update () {
		if(meshCollider==null) {
			meshCollider = GetComponent<MeshCollider> ();
			if(meshCollider==null) {
				meshCollider = GetComponentInChildren<MeshCollider>();
			}
		}
		if (mesh == null) {
			mesh = GetComponent<MeshFilter> ().mesh;
		}
		
		if (baseHeight == null)
			baseHeight = mesh.vertices;
		
		Vector3[] vertices = new Vector3[baseHeight.Length];
		for (int i=0;i<vertices.Length;i++)
		{
			var vertex = baseHeight[i];
			vertex.y += Mathf.Sin(Time.time * speed+ baseHeight[i].x + baseHeight[i].y + baseHeight[i].z) * scale;
			vertex.y += Mathf.PerlinNoise(baseHeight[i].x + noiseWalk, baseHeight[i].y + Mathf.Sin(Time.time * 0.1f)    ) * noiseStrength;
			vertices[i] = vertex;
		}
		mesh.vertices = vertices;
		mesh.RecalculateNormals();

		// Don't update our shared mesh if we have no mesh collider still
		if (meshCollider == null)
			return;

		meshCollider.sharedMesh = null;
		meshCollider.sharedMesh = mesh;
	}
}