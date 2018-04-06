//
//  RegisterViewController.swift
//  business-chat-app
//
//  Created by Timofei Sopin on 2018-03-02.
//  Copyright © 2018 Brogrammers. All rights reserved.
//

import UIKit
import Firebase

class RegisterScreenVC: UIViewController {
  
  @IBOutlet weak var usernameTextfield: UITextField!
  @IBOutlet weak var regButton: UIButton!
  @IBOutlet weak var emailTextfield: UITextField!
  @IBOutlet weak var passwordTextfield: UITextField!
  @IBOutlet weak var passwordConfirmTextfield: UITextField!
  
  let colours = Colours()
  
  
  
  override func viewDidLoad() {
    super.viewDidLoad()
    self.view.setGradient(colours.colourMainBlue.cgColor, colours.colourMainPurple.cgColor)
    regButton.layer.cornerRadius = 5 
    self.hideKeyboardWhenTappedAround() 
    
  }
  
  
  
  @IBAction func registerButtonPressed(_ sender: Any) {
    
    
    let email = emailTextfield.text
    let userName = usernameTextfield.text
    let password = passwordTextfield.text
    let confirmPassword = passwordConfirmTextfield.text
    
    if  password == confirmPassword && userName != nil && email != nil  {
      
      userRegister(userCreationComplete: { (success, loginError) in
        if success {
          self.presentStoryboard()
        } else {
          
          print(String(describing: loginError?.localizedDescription))
        }})
      
    }
    
    emailTextfield.resignFirstResponder()
    passwordTextfield.resignFirstResponder()
    
    
  }
  
  @IBAction func cancelBtn(_ sender: Any) {
    
    self.dismiss(animated: true, completion: nil)
    
  }
  
  
  func userRegister(userCreationComplete: @escaping (_ status: Bool, _ error: Error?) -> ()) {
    let userName = usernameTextfield.text!
    let password = passwordTextfield.text!
    let email = emailTextfield.text!
    
    
    Auth.auth().createUser(withEmail: "\(email)", password: "\(password)") { (user, error) in
      if let error = error {
        self.alert(message: (error.localizedDescription))
        
      } else {
        
        let userData = ["username": userName, "email": email, "avatar": false] as [String : Any]
        UserServices.instance.createDBUser(uid: (user?.uid)!, userData: userData)
        userCreationComplete(true, nil)
        
      }
      
    }
  }
}



