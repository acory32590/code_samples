using UnityEngine;
using System.Collections;

abstract class Wrapper : MonoBehaviour {
	protected Client simClient;
	public bool kill = false;
	public float rollPosModifier = 0.0f;
	public float pitchPosModifier = 0.0f;
	public bool useTransformRotation = true;

	void Start () {
		if(networkView.isMine) {
			simClient = new Client(4001, "10.10.101.90", 4001);
			StartCoroutine(checkTimer());
		}
	}
	
	protected void AssignRotation(float pitch, float roll) {
		float newPitch = pitchPosModifier;
		float newRoll = rollPosModifier;
		if(useTransformRotation) {
			newPitch += pitch;
			newRoll += roll;
		}
		simClient.packet.pitchPos = -Mathf.Clamp(newPitch, -20, 20);
		simClient.packet.rollPos = Mathf.Clamp(newRoll, -30, 30);
	}

	protected void checkKill() {
		if(Input.GetButtonDown("Kill")) {
			kill = true;
		}		
	}
	
	protected IEnumerator checkTimer() {
		while(true) {
			if(kill) {
				Debug.Log("KILLING!");
				audio.Play();
				break;
			}
			yield return new WaitForSeconds(0.033f);
			simClient.sendData();
		}
	}
}
